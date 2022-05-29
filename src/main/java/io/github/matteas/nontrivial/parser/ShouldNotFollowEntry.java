package io.github.matteas.nontrivial.parser;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ShouldNotFollowEntry<V extends Value<V>, K extends @NonNull Object> {
    public final Syntax.Disjunction<V, K> source;
    public final Set<K> disallowedKinds;

    public ShouldNotFollowEntry(Syntax.Disjunction<V, K> source, Set<K> disallowedKinds) {
        this.source = source;
        this.disallowedKinds = disallowedKinds;
    }
}