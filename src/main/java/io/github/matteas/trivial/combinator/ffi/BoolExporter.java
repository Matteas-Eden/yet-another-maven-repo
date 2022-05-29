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
        
        @Override
        public Combinator apply(Combinator argument) throws EvalError {
            throw new TypeError("Result is not a bool: Combinator attempted to invoke the bool as a function");
        }
    }

    public static final BoolExporter boolExporter = new BoolExporter();
    
    @Override
    public Boolean export(Combinator combinator) throws EvalError {
        try {
            return Optional.of(
                combinator
                    .apply(new DummyBoolCombinator(true))
                    .apply(new DummyBoolCombinator(false))
            )
                .filter(DummyBoolCombinator.class::isInstance)
                .map(DummyBoolCombinator.class::cast)
                .map(v -> v.value)
                .orElseThrow(() -> new TypeError("Result is not a bool: A different combinator was returned"));
        } catch (EvalError error) {
            throw new EvalError("Error exporting boolean", error);
        }
    }

    public static boolean exportBool(Combinator combinator) throws EvalError {
        return boolExporter.export(combinator);
    }
}
