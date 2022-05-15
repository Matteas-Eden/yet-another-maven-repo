package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

public class IntExporter implements Exporter<Integer> {
    public static final IntExporter intExporter = new IntExporter();
    
    @Override
    public Integer export(Combinator combinator) throws EvalError {
        final var pairExporter = new PairExporter<
            Integer,
            Integer,
            NatExporter,
            NatExporter
        >(NatExporter.natExporter, NatExporter.natExporter);
        try {
            final var pair = pairExporter.export(combinator);
            return pair.x - pair.y;
        } catch (EvalError error) {
            throw new EvalError("Error exporting integer", error);
        }
    }

    public static Integer exportInt(Combinator combinator) throws EvalError {
        return intExporter.export(combinator);
    }
}