package io.github.matteas.nontrivial.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.Set;
import java.util.HashSet;

/**
 * Also known as a graph, but they're pretending to be a tree.
 */
public abstract class CyclicTree<T extends CyclicTree<T>> {
    public abstract Iterator<T> children();

    protected abstract T self();

    /**
     * Monotonic set of past visitors that only grows over
     * time - never shrinks. If abused, this becomes a memory leak.
     */
    private final Set<Consumer<T>> visited = new HashSet<>();

    public void traversePostOrder(Consumer<T> visitor) {
        if (!visited.contains(visitor)) {
            visited.add(visitor);
            final var iterator = children();
            while (iterator.hasNext()) {
                iterator.next().traversePostOrder(visitor);
            }
            visitor.accept(self());
        }
    }
}