package io.github.matteas.trivial.ast;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import io.github.matteas.trivial.Scope;

public class AstApply implements Ast {
    final Ast function;
    final Ast argument;

    public AstApply(Ast function, Ast argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public Combinator eval(Scope scope) throws EvalError {
        try{
            return function.eval(scope).apply(argument.eval(scope));
        } catch (EvalError error) {
            throw new EvalError("Error evaluating function application: " + toString(), error);
        }
    }

    @Override
    public String toString() {
        return "Apply(" + function.toString() + ", " + argument.toString() + ")";
    }
}
