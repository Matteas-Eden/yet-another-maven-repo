package io.github.matteas.nontrivial.parser;

import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import io.github.matteas.nontrivial.lexer.Lexer;
import io.github.matteas.nontrivial.lexer.LexerRule;

/**
 * Syntax sugar for {@link Syntax} and {@link LexerRule}.
 */
public abstract class Language<
    C,
    V,
    K extends Language.TokenKind<C, T, K>,
    T extends Token<Language<C, V, K, T>.SimpleValue, K>
> {
    private final Class<K> kindClass;
    public final BinaryOperator<V> joiner;
    
    public Language(Class<K> kindClass, BinaryOperator<V> joiner) {
        this.kindClass = kindClass;
        this.joiner = joiner;
    }
    
    public Rule rule() {
        return new Rule();
    }

    public abstract K token(Object ... items);

    public abstract K repeat(Object ... items);

    public Lexer<C, T> lexer(List<K> tokenKind) {
        return new Lexer<>(
            tokenKind
                .stream()
                .map(TokenKind::toLexerRule)
                .collect(Collectors.toList())
        );
    }
    
    public Parser<SimpleValue, K, T> parser(Rule topLevelRule) {
        return topLevelRule.validate().match(
            validSyntax -> new Parser<>(validSyntax),
            badSyntax -> {
                // TODO: better error reporting
                throw new RuntimeException("Language contains conflicts");
            }
        );
    }

    public interface TokenKind<C, T, K extends TokenKind<C, T, K>> {
        LexerRule<C, T> toLexerRule();
        K or(Object ... items);
    }
    
    public class Rule extends Syntax.Deferred<SimpleValue, K> {
        private Syntax<SimpleValue, K> desugar(Object ... items) {
            if (items.length == 0) {
                throw new IllegalArgumentException("Parser rule must contain something");
            }
            
            Syntax<SimpleValue, K> head;
            if (kindClass.isInstance(items[0])) {
                head = new Syntax.Element<>(kindClass.cast(items[0]));
            } else if (getClass().isInstance(items[0])) {
                head = getClass().cast(items[0]);
            } else {
                throw new IllegalArgumentException("Items must be either a token kind or a rule of the right type. Instead, got: " + items[0] + " of type " + items[0].getClass().getSimpleName());
            }
            
            if (items.length == 1) {
                return head;
            }
            
            final var tail = Arrays.copyOfRange(items, 1, items.length);
            return new Syntax.Sequence<>(head, desugar(tail));
        }
        
        public Rule is(Object ... items) {
            assert !realized().isPresent();
            realize(desugar(items));
            return this;
        }
        
        public Rule or(Object ... items) {
            assert realized().isPresent();
            realize(new Syntax.Disjunction<>(realized().get(), desugar(items)));
            return this;
        }
        
        public Rule map(UnaryOperator<V> transformation) {
            assert realized().isPresent();
            realize(
                new Syntax.Transform<SimpleValue, K>(
                    v -> new SimpleValue(transformation.apply(v.value)),
                    realized().get()
                )
            );
            return this;
        }
    }

    public class SimpleValue implements Value<SimpleValue> {
        public final V value;
        
        public SimpleValue(V value) {
            this.value = value;
        }

        @Override
        public SimpleValue prepend(SimpleValue left) {
            return new SimpleValue(joiner.apply(left.value, value));
        }
    }
}