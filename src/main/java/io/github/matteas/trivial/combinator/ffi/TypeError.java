package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.EvalError;

public class TypeError extends EvalError {
    public TypeError(String reason) {
        super("Type Error: " + reason);
    }
}