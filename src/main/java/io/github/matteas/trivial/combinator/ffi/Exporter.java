package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

interface Exporter<T> {
    T export(Combinator combinator) throws EvalError;
}