package io.github.matteas.nontrivial.parser;

public interface Token<V extends Value<V>> extends Value<V> {
    TokenKind kind();
}
