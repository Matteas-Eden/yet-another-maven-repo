package io.github.matteas.nontrivial.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Collections;

/**
 * Also known as a graph, but they're pretending to be a tree.
 */
public abstract class CyclicTree<T extends CyclicTree<T>> {
    public abstract Iterator<T> children();

    protected abstract T self();

    private final Set<Consumer<T>> visited = Collections.newSetFromMap(new WeakHashMap<Consume<T>, Boolean>()); 

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