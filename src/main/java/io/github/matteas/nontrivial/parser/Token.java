package io.github.matteas.nontrivial.parser;

public interface Token<V extends Value<V>, K extends TokenKind> extends Value<V> {
    K kind();
}
