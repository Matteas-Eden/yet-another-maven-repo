package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;
import java.util.Optional;

public class NatExporter implements Exporter<Integer> {
    private static class DummyNatCombinator implements Combinator {
        public final int value;
        
        private DummyNatCombinator(int value) {
            this.value = value;
        }
        
        @Override
        public Combinator apply(Combinator argument) throws EvalError {
            throw new EvalError("Type Error: Result is not a nat");
        }
    }

    public static final NatExporter natExporter = new NatExporter();
    
    @Override
    public Integer export(Combinator combinator) throws EvalError {
        return Optional.of(
            combinator
                .apply(n -> {
                    if (!(n instanceof DummyNatCombinator)) {
                        throw new EvalError("Type Error: Result is not a nat");
                    }
                    final var nat = (DummyNatCombinator)n;
                    return new DummyNatCombinator(nat.value + 1);
                })
                .apply(new DummyNatCombinator(0))
        )
            .filter(DummyNatCombinator.class::isInstance)
            .map(DummyNatCombinator.class::cast)
            .map(v -> v.value)
            .orElseThrow(() -> new EvalError("Type Error: Result is not a nat"));
    }

    public static Integer exportNat(Combinator combinator) throws EvalError {
        return natExporter.export(combinator);
    }
}
