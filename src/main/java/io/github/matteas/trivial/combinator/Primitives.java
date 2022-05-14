package io.github.matteas.trivial.combinator;

import io.github.matteas.trivial.combinator.ffi.BoolExporter;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.exportBool;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.boolExporter;
import io.github.matteas.trivial.combinator.ffi.PairExporter;
import java.util.Map;

public class Primitives {
    public static final Map<String, Combinator> PRIMITIVES = Map.of(
        // Language Primitives
        
        "I", a -> a,
        "K", a -> b -> a,
        "S", a -> b -> c -> a.apply(c).apply(b.apply(c)),

        // Foreign Function Interface

        "Print.Bool", bool -> {
            System.out.println(exportBool(bool));
            return bool;
        },
        "Print.PairBoolBool", pair -> {
            final var exporter = new PairExporter<
                Boolean,
                Boolean,
                BoolExporter,
                BoolExporter
            >(boolExporter, boolExporter);
            System.out.println(exporter.export(pair));
            return pair;
        }
    );
}
