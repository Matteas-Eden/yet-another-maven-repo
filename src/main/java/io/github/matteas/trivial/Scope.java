package io.github.matteas.trivial;

import java.util.Map;
import java.util.HashMap;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

public class Scope {
    public class NameNotFoundException extends EvalError {
        public NameNotFoundException(String name) {
            super(String.format("Cannot find definition for identifier '%s'", name));
        }
    }
    
    private final Map<String, Combinator> names;
    // TODO: Sub namespaces, import statements, etc.

    public Scope(Map<String, Combinator> primitives) {
        this.names = new HashMap<>(primitives);
    }

    public void assign(String name, Combinator combinator) {
        names.put(name, combinator);
    }

    public Combinator resolve(String name) throws NameNotFoundException {
        if (!names.containsKey(name)) {
            throw new NameNotFoundException(name);
        }
        return names.get(name);
    }
}