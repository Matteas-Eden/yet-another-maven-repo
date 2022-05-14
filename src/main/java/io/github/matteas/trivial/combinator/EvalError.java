package io.github.matteas.trivial.combinator;

public class EvalError extends Exception {
    public EvalError(String reason) {
        super("Type Error: " + reason);
    }
}
