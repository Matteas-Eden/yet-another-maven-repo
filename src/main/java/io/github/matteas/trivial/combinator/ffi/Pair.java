package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Pair<X, Y> implements Combinator {
    public final X x;
    public final Y y;

    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Combinator apply(Combinator argument) throws EvalError {
        throw new TypeError("Result is not a pair: Combinator attempted to invoke the Pair as a function");
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(x) + "," + String.valueOf(y) + ")";
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        final var otherPair = getClass().cast(other);
        return Objects.equals(x, otherPair.x) && Objects.equals(y, otherPair.y);
    }
}
