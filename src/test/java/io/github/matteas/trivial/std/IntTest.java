package io.github.matteas.trivial.std;

import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;

import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.junitpioneer.jupiter.params.IntRangeSource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.matteas.trivial.Repl;

import static io.github.matteas.trivial.combinator.ffi.IntExporter.exportInt;
import static io.github.matteas.trivial.combinator.ffi.NatExporter.exportNat;

@org.junit.jupiter.api.Disabled
class IntTest {
    static final int MAX_INT_TO_TEST = 2;
    
    enum UnaryOperation {
        Succ("Neg", (x) -> -x);
        // TODO: other interesting stuff like factorial
        
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
        Sub("Sub", (x, y) -> x - y),
        Mul("Mul", (x, y) -> x * y);
        //Div("Div", (x, y) -> x / y); TODO
        //Pow("Pow", (x, y) -> (int)Math.pow((double)x, (double)y)); TODO
        
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
    
    @CartesianTest(name = "Number Int.{0} is exported as the number {0}")
    void constants(
        @IntRangeSource(from = -100, to = 100, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(x, exportInt(repl.eval("Int." + x).get()));
    }
    
    @CartesianTest(name = "Unary operation Int.{0} on Int.{1} is correct")
    void unaryOperations(
        @Enum UnaryOperation operation,
        @IntRangeSource(from = -MAX_INT_TO_TEST, to = MAX_INT_TO_TEST, closed = true) int x
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            operation.apply(x),
            exportInt(repl.eval(String.format(
                "Int.%s Int.%d",
                operation.name,
                x
            )).get())
        );
    }

    @CartesianTest(name = "Int.Mul._helperMulFirstFirst on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperMulFirstFirst(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            a * c,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperMulFirstFirst (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }

    @CartesianTest(name = "Int.Mul._helperMulFirstSecond on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperMulFirstSecond(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            a * d,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperMulFirstSecond (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }

    @CartesianTest(name = "Int.Mul._helperMulSecondFirst on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperMulSecondFirst(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            b * c,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperMulSecondFirst (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }

    @CartesianTest(name = "Int.Mul._helperMulSecondSecond on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperMulSecondSecond(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            b * d,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperMulSecondSecond (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }

    @CartesianTest(name = "Int.Mul._helperSumOfProductsSame on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperSumOfProductsSame(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            a * c + b * d,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperSumOfProductsSame (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }

    @CartesianTest(name = "Int.Mul._helperSumOfProductsDifferent on (Pair Nat.{0} Nat.{1}) and (Pair Nat.{2} Nat.{3}) is correct")
    void mulHelperSumOfProductsDifferent(
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int a,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int b,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int c,
        @IntRangeSource(from = 0, to = MAX_INT_TO_TEST, closed = true) int d
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            a * d + b * c,
            exportNat(repl.eval(String.format(
                "Int.Mul._helperSumOfProductsDifferent (Pair Nat.%d Nat.%d) (Pair Nat.%d Nat.%d)",
                a, b, c, d
            )).get()));
    }
    
    @CartesianTest(name = "Binary operation Int.{0} on Int.{1} and Int.{2} is correct")
    void binaryOperations(
        @Enum BinaryOperation operation,
        @IntRangeSource(from = -MAX_INT_TO_TEST, to = MAX_INT_TO_TEST, closed = true) int x,
        @IntRangeSource(from = -MAX_INT_TO_TEST, to = MAX_INT_TO_TEST, closed = true) int y
    ) throws Exception {
        final var repl = new Repl();
        assertEquals(
            operation.apply(x, y),
            exportInt(repl.eval(String.format(
                "Int.%s Int.%d Int.%d",
                operation.name, x, y
            )).get())
        );
    }
}
