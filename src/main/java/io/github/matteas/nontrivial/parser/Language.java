package io.github.matteas.nontrivial.parser;

import java.util.function.UnaryOperator;
import java.util.Map;
import java.util.Arrays;

/**
 * Syntax sugar for {@link Syntax}.
 */
public class Language<V extends Value<V>, KK, K extends TokenKind> {
    public final Map<KK, K> dictionary;
    public final Class<KK> kindKeyClass;

    public Language(Map<KK, K> dictionary, Class<KK> kindKeyClass) {
        this.dictionary = dictionary;
        this.kindKeyClass = kindKeyClass;
    }
    
    public Rule<V, KK, K> rule() {
        return new Rule<>(dictionary, kindKeyClass);
    }
    
    public static class Rule<V extends Value<V>, KK, K extends TokenKind> extends Syntax.Deferred<V, K> {
        public final Map<KK, K> dictionary;
        public final Class<KK> kindKeyClass;
        
        public Rule(Map<KK, K> dictionary, Class<KK> kindKeyClass) {
            this.dictionary = dictionary;
            this.kindKeyClass = kindKeyClass;
        }

        private Syntax<V, K> desugar(Object ... items) {
            if (items.length == 0) {
                throw new IllegalArgumentException("Sequence must contain something");
            }
            Syntax<V, K> head;
            if (kindKeyClass.isInstance(items[0])) {
                head = new Syntax.Element<>(dictionary.get(kindKeyClass.cast(items[0])));
            } else if (getClass().isInstance(items[0])) {
                head = getClass().cast(items[0]);
            } else {
                throw new IllegalArgumentException("Items must be either a token kind identifier or a rule of the right type");
            }
            if (items.length == 1) {
                return head;
            }
            final var tail = Arrays.copyOfRange(items, 1, items.length);
            return new Syntax.Sequence<>(head, desugar(tail));
        }
        
        public Rule<V, KK, K> is(Object ... items) {
            assert !realized().isPresent();
            realize(desugar(items));
            return this;
        }
        
        public Rule<V, KK, K> or(Object ... items) {
            assert realized().isPresent();
            realize(new Syntax.Disjunction<>(realized().get(), desugar(item)));
            return this;
        }
        
        public Rule<V, KK, K> map(UnaryOperator<V> transformation) {
            assert realized().isPresent();
            realize(new Syntax.Transform<V, K>(transformation, realized().get()));
            return this;
        }
    }
}