package io.github.matteas.nontrivial.parser;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import java.util.Iterator;

import io.github.matteas.nontrivial.lexer.LexerRule;
import io.github.matteas.nontrivial.lexer.RegularExpression;
import io.github.matteas.nontrivial.util.CharacterIterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;

public class StringLanguage<V> extends Language<
    @NonNull Character,
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
        return desugarIterator(Arrays.stream(items).iterator());
    }
    
    private RegularExpression<Character> desugarIterator(Iterator<? extends @NonNull Object> items) {
        if (!items.hasNext()) {
            throw new IllegalArgumentException("Token must contain something");
        }

        final var headItem = items.next();
        
        RegularExpression<Character> head;
        if (headItem instanceof String) {
            final var string = (String)headItem;
            head = desugarIterator(new CharacterIterator(string));
        } else if (headItem instanceof Character) {
            final var character = (Character)headItem;
            head = new RegularExpression.Character<>(character);
        } else if (headItem instanceof RegularExpression<?>) {
            head = (RegularExpression<Character>)headItem;
        } else {
            throw new IllegalArgumentException("Items must be either a string, a character, or a RegularExpression. Instead, got: " + headItem + " of type " + headItem.getClass().getSimpleName());
        }
        
        if (!items.hasNext()) {
            return head;
        }
        
        return new RegularExpression.Sequence<>(head, desugarIterator(items));
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

        @Override
        public String toString() {
            return String.format("Token(%s)", contents);
        }
    }

    public class StringTokenKind implements Language.TokenKind<Character, StringToken, StringTokenKind> {
        public final RegularExpression<Character> expression;
        public String debugName = "Unnamed";
        
        public StringTokenKind(RegularExpression<Character> expression) {
            this.expression = expression;
        }
        
        @Override
        public LexerRule<Character, StringToken> toLexerRule() {
            return new LexerRule<>(
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
        public StringTokenKind or(Object ... items) {
            return new StringTokenKind(
                new RegularExpression.Disjunction<Character>(
                    expression,
                    desugar(items)
                )
            );
        }

        public StringTokenKind withDebugName(String debugName) {
            this.debugName = debugName;
            return this;
        }

        @Override
        public String toString() {
            return String.format("TokenKind(%s)", debugName);
        }
    }
}