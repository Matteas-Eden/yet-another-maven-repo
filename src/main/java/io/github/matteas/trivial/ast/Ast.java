package io.github.matteas.trivial.ast;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

import java.util.Map;

public interface Ast {
    Combinator eval(Map<String, Combinator> scope) throws EvalError;
}
