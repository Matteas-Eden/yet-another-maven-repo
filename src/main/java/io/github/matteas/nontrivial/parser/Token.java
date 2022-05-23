package io.github.matteas.nontrivial.parser;

public interface Token<V extends Value<V>, K extends TokenKind> {
    K kind();
    V value();
}
