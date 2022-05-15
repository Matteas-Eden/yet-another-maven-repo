package io.github.matteas.trivial.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.matteas.trivial.combinator.ffi.Pair;
import io.github.matteas.trivial.Repl;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.boolExporter;
import io.github.matteas.trivial.combinator.ffi.BoolExporter;
import io.github.matteas.trivial.combinator.ffi.PairExporter;

class PairTest {
    @Test
    void pair() throws Exception {
        final var repl = new Repl();
        final var exporter = new PairExporter<
            Boolean,
            Boolean,
            BoolExporter,
            BoolExporter
        >(boolExporter, boolExporter);
        assertEquals(new Pair<>(true, true), exporter.export(repl.eval("Pair Bool.True Bool.True").get()));
        assertEquals(new Pair<>(false, true), exporter.export(repl.eval("Pair Bool.False Bool.True").get()));
    }
    
    @Test
    void nested() throws Exception {
        final var repl = new Repl();
        final var exporter = new PairExporter<
            Boolean,
            Pair<Boolean, Pair<Boolean, Boolean>>,
            BoolExporter,
            PairExporter<
                Boolean,
                Pair<Boolean, Boolean>,
                BoolExporter,
                PairExporter<
                    Boolean,
                    Boolean,
                    BoolExporter,
                    BoolExporter
                >
            >
        >(
            boolExporter,
            new PairExporter<>(
                boolExporter,
                new PairExporter<>(
                    boolExporter,
                    boolExporter
                )
            )
        );
        assertEquals(new Pair<>(true, new Pair<>(false, new Pair<>(true, false))), exporter.export(repl.eval("Pair Bool.True (Pair Bool.False (Pair Bool.True Bool.False))").get()));
        assertEquals(new Pair<>(false, new Pair<>(false, new Pair<>(true, true))), exporter.export(repl.eval("Pair Bool.False (Pair Bool.False (Pair Bool.True Bool.True))").get()));
    }
}
