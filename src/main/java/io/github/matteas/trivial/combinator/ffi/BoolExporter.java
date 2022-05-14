package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import java.util.Optional;

public class BoolExporter implements Exporter<Boolean> {
    private static class DummyBoolCombinator implements Combinator {
        public final boolean value;
        
        private DummyBoolCombinator(boolean value) {
            this.value = value;
        }
        
        public Combinator apply(Combinator argument) throws EvalError {
            throw new EvalError("Type Error: Result is not a bool");
        }
    }
    
    public Boolean export(Combinator combinator) throws EvalError {
        return Optional.of(
            combinator
                .apply(new DummyBoolCombinator(true))
                .apply(new DummyBoolCombinator(false))
        )
            .filter(DummyBoolCombinator.class::isInstance)
            .map(DummyBoolCombinator.class::cast)
            .map(v -> v.value)
            .orElseThrow(() -> new EvalError("Type Error: Result is not a bool"));
    }
}
