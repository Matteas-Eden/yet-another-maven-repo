package io.github.matteas.nontrivial.parser;

public interface Token<V extends Value<V>, K> {
    K kind();
    V value();
}
