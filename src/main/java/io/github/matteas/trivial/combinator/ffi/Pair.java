package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

public class Pair<X, Y> implements Combinator {
    public final X x;
    public final Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public Combinator apply(Combinator argument) throws EvalError {
        throw new EvalError("Type Error: Result is not a pair");
    }

    public String toString() {
        return "(" + x.toString() + "," + y.toString() + ")";
    }
}
