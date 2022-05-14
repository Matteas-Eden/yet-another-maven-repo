package io.github.matteas.trivial.ast;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import java.util.Map;

public class AstApply implements Ast {
    final Ast function;
    final Ast argument;

    public AstApply(Ast function, Ast argument) {
        this.function = function;
        this.argument = argument;
    }

    public Combinator eval(Map<String, Combinator> scope) throws EvalError {
        return function.eval(scope).apply(argument.eval(scope));
    }

    public String toString() {
        return "Apply(" + function.toString() + ", " + argument.toString() + ")";
    }
}
