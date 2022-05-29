package io.github.matteas.nontrivial.parser;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

interface ValidationResult<V extends Value<V>, K extends @NonNull Object> {
    <T> T match(Function<ValidSyntax<V, K>, T> success, Function<Syntax<V, K>, T> failure);
    
    public class Ok<V extends Value<V>, K extends @NonNull Object> implements ValidationResult<V, K> {
        public final ValidSyntax<V, K> syntax;
        
        public Ok(ValidSyntax<V, K> syntax) {
            this.syntax = syntax;
        }

        @Override
        public <T> T match(Function<ValidSyntax<V, K>, T> success, Function<Syntax<V, K>, T> failure) {
            return success.apply(syntax);
        }
    }
    
    public class Error<V extends Value<V>, K extends @NonNull Object> implements ValidationResult<V, K> {
        public final Syntax<V, K> syntax;
        
        public Error(Syntax<V, K> syntax) {
            this.syntax = syntax;
        }

        @Override
        public <T> T match(Function<ValidSyntax<V, K>, T> success, Function<Syntax<V, K>, T> failure) {
            return failure.apply(syntax);
        }
    }
}
