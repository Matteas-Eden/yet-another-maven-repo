package io.github.matteas.nontrivial.parser;

interface ValidationResult {
    <T> T match(Function<Ok, T> success, Function<Error, T> failure);
    
    public class Ok implements ValidationResult {
        public final ValidSyntax syntax;
        
        public Ok(ValidSyntax syntax) {
            this.syntax = syntax;
        }

        <T> T match(Function<ValidSyntax, T> success, Function<Syntax, T> failure) {
            return success.apply(syntax);
        }
    }
    
    public class Error implements ValidationResult {
        public final Syntax syntax;
        
        public Error(Syntax syntax) {
            this.syntax = syntax;
        }

        <T> T match(Function<ValidSyntax, T> success, Function<Syntax, T> failure) {
            return failure.apply(syntax);
        }
    }
}
