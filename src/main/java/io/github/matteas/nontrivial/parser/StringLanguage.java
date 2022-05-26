package io.github.matteas.nontrivial.parser;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import java.util.Iterator;
import io.github.matteas.nontrivial.lexer.LexerRule;
import io.github.matteas.nontrivial.lexer.RegularExpression;
import io.github.matteas.nontrivial.lexer.CharacterIterator;

public class StringLanguage<V> extends Language<
    Character,
    V,
    StringLanguage<V>.StringTokenKind,
    StringLanguage<V>.StringToken
> {
    public final BiFunction<String, StringTokenKind, V> tokenToValue;

    public StringLanguage(
        BiFunction<String, StringTokenKind, V> tokenToValue,
        BinaryOperator<V> joiner,
        Class<StringTokenKind> kindClass // eww - we can't do StringToken.class due to type erasure
    ) {
        super(kindClass, joiner);
        
        this.tokenToValue = tokenToValue;
    }

    private RegularExpression<Character> desugar(Object ... items) {
        return desugar(Arrays.stream(items).iterator());
    }
    
    private RegularExpression<Character> desugar(Iterator<Object> items) {
        if (!items.hasNext()) {
            throw new IllegalArgumentException("Sequence must contain something");
        }

        final var headItem = items.next();
        
        RegularExpression<Character> head;
        if (headItem instanceof String) {
            final var string = (String)headItem;
            head = desugar(new CharacterIterator(string));
        } else if (headItem instanceof Character) {
            final var character = (Character)headItem;
            head = new RegularExpression.Character<>(character);
        } else if (headItem instanceof RegularExpression<?>) {
            head = (RegularExpression<Character>)headItem;
        } else {
            throw new IllegalArgumentException("Items must be either a string, a character, or a RegularExpression");
        }
        
        if (!items.hasNext()) {
            return head;
        }
        
        return new RegularExpression.Sequence<>(head, desugar(items));
    }

    @Override
    public StringTokenKind token(Object ... items) {
        return new StringTokenKind(desugar(items));
    }

    @Override
    public StringTokenKind repeat(Object ... items) {
        return new StringTokenKind(
            new RegularExpression.Repetition<Character>(
                desugar(items)
            )
        );
    }
    
    public class StringToken implements Token<
        Language<
            Character,
            V,
            StringLanguage<V>.StringTokenKind,
            StringLanguage<V>.StringToken
        >.SimpleValue,
        StringTokenKind
    > {
        public final StringTokenKind kind;
        public final String contents;

        public StringToken(StringTokenKind kind, String contents) {
            this.kind = kind;
            this.contents = contents;
        }

        @Override
        public StringTokenKind kind() {
            return kind;
        }
        
        @Override
        public SimpleValue value() {
            return new SimpleValue(tokenToValue.apply(contents, kind));
        }
    }

    public class StringTokenKind implements Language.TokenKind<Character, StringToken, StringTokenKind> {
        public final LexerRule<Character, StringToken> lexerRule;
        
        public StringTokenKind(RegularExpression<Character> expression) {
            lexerRule = new LexerRule<>(
                expression,
                contents -> {
                    final var builder = new StringBuilder(contents.size());
                    for (Character c : contents) {
                        builder.append(c);
                    }
                    return new StringToken(
                        this,
                        builder.toString()
                    );
                }
            );
        }
        
        @Override
        public LexerRule<Character, StringToken> toLexerRule() {
            return lexerRule;
        }

        @Override
        public StringTokenKind or(Object ... items) {
            return new StringTokenKind(
                new RegularExpression.Disjunction<Character>(
                    lexerRule.expression,
                    desugar(items)
                )
            );
        }
    }
}