package io.github.matteas.trivial.ast;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import io.github.matteas.trivial.Scope;

public interface Ast {
    Combinator eval(Scope scope) throws EvalError;
}
