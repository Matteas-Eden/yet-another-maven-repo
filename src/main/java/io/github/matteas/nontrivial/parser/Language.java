package io.github.matteas.nontrivial.parser;

import java.util.function.UnaryOperator;
import java.util.Map;
import java.util.Arrays;

/**
 * Syntax sugar for {@link Syntax}.
 */
public class Language<V extends Value<V>, K extends TokenKind> {
    public final Map<String, K> dictionary;

    public Language(Map<String, K> dictionary) {
        this.dictionary = dictionary;
    }
    
    public Rule<V, K> rule() {
        return new Rule<>(dictionary);
    }

    public Rule<V, K> seq(Syntax<V, K> ... syntaxes) {
        if (syntaxes.length == 0) {
            throw new IllegalArgumentException("Sequence must have at least one syntax");
        }
        if (syntaxes.length == 1) {
            return rule().is(syntaxes[0]);
        }
        final var tail = Arrays.copyOfRange(syntaxes, 1, syntaxes.length);
        return rule().is(new Syntax.Sequence<>(syntaxes[0], seq(tail)));
    }
    
    public static class Rule<V extends Value<V>, K extends TokenKind> extends Syntax.Deferred<V, K> {
        public final Map<String, K> dictionary;
        
        public Rule(Map<String, K> dictionary) {
            this.dictionary = dictionary;
        }
        
        public Rule<V, K> is(Syntax<V, K> ... syntaxes) {
            assert !realized().isPresent();
            return is(seq(syntaxes));
        }
        
        public Rule<V, K> is(String string) {
            assert !realized().isPresent();
            return is(dictionary.get(string));
        }
        
        public Rule<V, K> is(K kind) {
            assert !realized().isPresent();
            return is(new Syntax.Element<>(kind));
        }

        public Rule<V, K> is(Syntax<V, K> syntax) {
            assert !realized().isPresent();
            realize(syntax);
            return this;
        }
        
        public Rule<V, K> or(Syntax<V, K> ... syntaxes) {
            assert realized().isPresent();
            return or(seq(syntaxes));
        }
        
        public Rule<V, K> or(String string) {
            assert realized().isPresent();
            return or(dictionary.get(string));
        }
        
        public Rule<V, K> or(K kind) {
            assert realized().isPresent();
            return or(new Syntax.Element<>(kind));
        }
        
        public Rule<V, K> or(Syntax<V, K> syntax) {
            assert realized().isPresent();
            realize(new Syntax.Disjunction<>(realized().get(), syntax));
            return this;
        }
        
        public Rule<V, K> map(UnaryOperator<V> transformation) {
            assert realized().isPresent();
            realize(new Syntax.Transform<V, K>(transformation, realized().get()));
            return this;
        }
    }
}