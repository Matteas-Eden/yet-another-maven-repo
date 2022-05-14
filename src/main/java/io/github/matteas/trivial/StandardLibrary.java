package io.github.matteas.trivial;

public class StandardLibrary {
    public static final String STANDARD_LIBRARY = ""
        + "B = S (K S) K;" // Bluebird. \abc->a(bc)
        + "C = S (S (K (S (K S) K)) S) (K K);" // Cardinal. \abc->acb
        + "W = S S (S K);"
        + "B1 = B B B;" // Blackbird. \abcd->a(bcd)
        + "M = S I I;" // Mockingbird. \a->aa
        + "T = C I;" // Thrush. \ab->ba
        + "Fn.Identity = I;"
        + "Fn.Compose = B;"
        + "Fn.Swap2nd3rd = C;"
        + "Fn.Discard2nd = K;"
        + "Fn.Duplicate2nd = W;"
        + "Fn.Compose2 = B1;"
        + "Bool.True = K;"
        + "Bool.False = K I;"
        + "Bool.IfElse = I;"
        + "Bool.Not = C;"
        + "Bool.Or = M;"
        + "Bool.And = S S K;"
        + "Bool.Xor = C (B S (C B C)) I;"
        + "Bool.Eq = Fn.Compose2 Bool.Not Bool.Xor;"
        + "Pair = B C T;"
        + "Pair.First = T K;"
        + "Pair.Second = T (K I);"
        + "Pair.Swap = T (C Pair);"
        //+ "Pair.ShiftRight = ;"
        //+ "Pair.ShiftLeft = ;"
        + "Nat.0 = I;"
        + "Nat.Successor = B (S B) I;"
        + "Nat.Add = C I Nat.Successor;"
        + "Nat.Mult = B;"
        + "Nat.Exp = B (C I) I;"
        ;
}