package io.github.matteas.trivial.std;

import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;
import java.util.function.BiPredicate;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.junitpioneer.jupiter.params.IntRangeSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.matteas.trivial.Repl;

import static io.github.matteas.trivial.combinator.ffi.NatExporter.exportNat;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.exportBool;

class NatTest {
    static final int MAX_NAT_TO_TEST= 3;
    
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
    }
    
    enum BinaryOperation {
        Add("Add", (x, y) -> x + y),
        Sub("Sub", (x, y) -> x - y),
        Mul("Mul", (x, y) -> x * y);
        //Div("Div", (x, y) -> x / y); TODO
        
        public final String name;
        private final BinaryOperator<Integer> fn;
        
        BinaryOperation(String name, BinaryOperator<Integer> fn) {
            this.name = name;
            this.fn = fn;
        }
        
        public int apply(int x, int y) {
            return fn.apply(x, y);
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
    }
    
    @CartesianTest
    void constants(
        @IntRangeSource(from = 0, to = MAX_NAT_TO_TEST, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(x, exportNat(repl.eval("Nat." + x).get()));
    }
    
    @CartesianTest
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
    
    @CartesianTest
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
    
    @CartesianTest
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
    
    @CartesianTest
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
