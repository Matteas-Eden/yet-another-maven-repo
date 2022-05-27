package io.github.matteas.nontrivial.parser;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Value<V extends @NonNull Value<V>> {
    V prepend(V value);
}
