package io.github.matteas.trivial.combinator;

@FunctionalInterface
public interface Combinator {
    Combinator apply(Combinator argument) throws EvalError;
}