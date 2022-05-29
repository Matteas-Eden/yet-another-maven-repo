package io.github.matteas.trivial.combinator.ffi;

import io.github.matteas.trivial.combinator.Combinator;
import io.github.matteas.trivial.combinator.EvalError;

import java.util.Optional;

public class PairExporter<
    X,
    Y,
    XE extends Exporter<X>,
    YE extends Exporter<Y>
> implements Exporter<Pair<X, Y>> {
    private final XE xExporter;
    private final YE yExporter;
    
    public PairExporter(XE xExporter, YE yExporter) {
        this.xExporter = xExporter;
        this.yExporter = yExporter;
    }

    // Type Safety: The only way to get a Pair is from the Pair<X,Y> we
    // supplied, and so checking against the raw Pair.class::isInstance
    // is enough.
    @SuppressWarnings("unchecked")
    @Override
    public Pair<X, Y> export(Combinator combinator) throws EvalError {
        try {
            return Optional.of(
                    combinator.apply(x -> y -> new Pair<>(
                        xExporter.export(x),
                        yExporter.export(y)
                    ))
            )
                .filter(Pair.class::isInstance)
                .map(Pair.class::cast)
                .orElseThrow(() -> new TypeError("Result is not a pair: A different combinator was returned"));
        } catch (EvalError error) {
            throw new EvalError("Error exporting pair", error);
        }
    }
}
