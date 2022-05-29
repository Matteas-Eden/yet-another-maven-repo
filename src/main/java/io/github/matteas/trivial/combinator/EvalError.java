package io.github.matteas.trivial.combinator;

public class EvalError extends Exception {
    public EvalError(String reason) {
        super("Evaluation Error: " + reason);
    }
    
    public EvalError(String reason, Throwable cause) {
        super("Evaluation Error: " + reason, cause);
    }
}
