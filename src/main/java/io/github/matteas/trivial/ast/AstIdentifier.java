package io.github.matteas.trivial.ast;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import java.util.Map;

public class AstIdentifier implements Ast {
    final String identifier;
    
    public AstIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public Combinator eval(Map<String, Combinator> scope) throws EvalError {
        // System.out.println("Id[" + identifier + "].eval");
        return scope.get(identifier);
    }
    
    public String toString() {
        return "Id('" + identifier + "')";
    }
}
