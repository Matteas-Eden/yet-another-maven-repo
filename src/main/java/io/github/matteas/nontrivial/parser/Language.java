package io.github.matteas.nontrivial.parser;

import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;
import io.github.matteas.nontrivial.lexer.Lexer;
import io.github.matteas.nontrivial.lexer.LexerRule;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Syntax sugar for {@link Syntax} and {@link LexerRule}.
 */
public abstract class Language<
    C extends @NonNull Object,
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
            return desugarIterator(Arrays.stream(items).iterator());
        }
        
        private Syntax<SimpleValue, K> desugarIterator(Iterator<? extends @NonNull Object> items) {
            if (!items.hasNext()) {
                throw new IllegalArgumentException("Parser rule must contain something");
            }
    
            final var headItem = items.next();
            
            Syntax<SimpleValue, K> head;
            if (kindClass.isInstance(headItem)) {
                head = new Syntax.Element<>(kindClass.cast(headItem));
            } else if (getClass().isInstance(headItem)) {
                head = getClass().cast(headItem);
            } else {
                throw new IllegalArgumentException("Items must be either a token kind or a rule of the right type. Instead, got: " + headItem + " of type " + headItem.getClass().getSimpleName());
            }
            
            if (!items.hasNext()) {
                return head;
            }
            
            return new Syntax.Sequence<>(head, desugarIterator(items));
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

        @Override
        public String toString() {
            return String.format("Value(%s)", value);
        }
    }
}