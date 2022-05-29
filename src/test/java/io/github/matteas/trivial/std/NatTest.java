package io.github.matteas.trivial.std;

import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import java.util.function.BiPredicate;

import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.junitpioneer.jupiter.params.IntRangeSource;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.matteas.trivial.Repl;

import static io.github.matteas.trivial.combinator.ffi.NatExporter.exportNat;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.exportBool;
import io.github.matteas.trivial.combinator.ffi.Pair;
import io.github.matteas.trivial.combinator.ffi.PairExporter;
import io.github.matteas.trivial.combinator.ffi.NatExporter;
import static io.github.matteas.trivial.combinator.ffi.NatExporter.natExporter;

@org.junit.jupiter.api.Disabled
class NatTest {
    static final int MAX_NAT_TO_TEST = 4;
    
    enum UnaryOperation {
        Succ("Succ", (x) -> x + 1),
        Pred("Pred", (x) -> x > 0 ? x - 1 : x);
        
        public final String name;
        private final UnaryOperator<Integer> fn;
        
        UnaryOperation(String name, UnaryOperator<Integer> fn) {
            this.name = name;
            this.fn = fn;
        }
        
        public int apply(int x) {
            return fn.apply(x);
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    enum BinaryOperation {
        Add("Add", (x, y) -> x + y),
        Sub("Sub", (x, y) -> x > y ? x - y : 0),
        Mul("Mul", (x, y) -> x * y),
        //Div("Div", (x, y) -> x / y); TODO
        Pow("Pow", (x, y) -> (int)Math.pow((double)x, (double)y));
        
        public final String name;
        private final BinaryOperator<Integer> fn;
        
        BinaryOperation(String name, BinaryOperator<Integer> fn) {
            this.name = name;
            this.fn = fn;
        }
        
        public int apply(int x, int y) {
            return fn.apply(x, y);
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    enum Predicate {
        Eq("Eq", (x, y) -> x == y),
        Leq("Leq", (x, y) -> x <= y);
        
        public final String name;
        private final BiPredicate<Integer, Integer> fn;
        
        Predicate(String name, BiPredicate<Integer, Integer> fn) {
            this.name = name;
            this.fn = fn;
        }
        
        public boolean test(int x, int y) {
            return fn.test(x, y);
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    @CartesianTest(name = "Number Nat.{0} is exported as the number {0}")
    void constants(
        @IntRangeSource(from = 0, to = 100, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(x, exportNat(repl.eval("Nat." + x).get()));
    }
    
    @CartesianTest(name = "Unary operation Nat.{0} on Nat.{1} is correct")
    void unaryOperations(
        @Enum UnaryOperation operation,
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            operation.apply(x),
            exportNat(repl.eval(String.format(
                "Nat.%s Nat.%d",
                operation.name,
                x
            )).get())
        );
    }

    @Test
    void predecessorHelper() throws Exception {
        final var repl = new Repl();
        final var exporter = new PairExporter<
            Integer,
            Integer,
            NatExporter,
            NatExporter
        >(natExporter, natExporter);
        assertEquals(
            new Pair<>(0, 1),
            exporter.export(repl.eval("Nat.Pred._helperIncrement (Pair Nat.0 Nat.0)").get())
        );
        assertEquals(
            new Pair<>(1, 2),
            exporter.export(repl.eval("Nat.Pred._helperIncrement (Pair Nat.0 Nat.1)").get())
        );
        assertEquals(
            new Pair<>(2, 3),
            exporter.export(repl.eval("Nat.Pred._helperIncrement (Pair Nat.1 Nat.2)").get())
        );
    }
    
    @CartesianTest(name = "Binary operation Nat.{0} on Nat.{1} and Nat.{2} is correct")
    void binaryOperations(
        @Enum BinaryOperation operation,
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int x,
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int y
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            operation.apply(x, y),
            exportNat(repl.eval(String.format(
                "Nat.%s Nat.%d Nat.%d",
                operation.name,
                x,
                y
            )).get())
        );
    }
    
    @CartesianTest(name = "Predicate Nat.{0} on Nat.{1} and Nat.{2} is correct")
    void predicates(
        @Enum Predicate predicate,
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int x,
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int y
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            predicate.test(x, y),
            exportBool(repl.eval(String.format(
                "Nat.%s Nat.%d Nat.%d",
                predicate.name,
                x,
                y
            )).get())
        );
    }
    
    @CartesianTest(name = "Nat.IsZero on Nat.{0} is correct")
    void isZero(
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            x == 0,
            exportBool(repl.eval(String.format(
                "Nat.IsZero Nat.%d",
                x
            )).get())
        );
    }
}
