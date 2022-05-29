package io.github.matteas.nontrivial.parser;

public interface Value<V extends Value<V>> {
    V prepend(V value);
}
