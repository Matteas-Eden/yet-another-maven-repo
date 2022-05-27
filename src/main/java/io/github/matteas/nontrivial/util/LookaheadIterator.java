package io.github.matteas.nontrivial.util;

import java.util.Iterator;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;

public class LookaheadIterator<T> implements Iterator<T> {
    private final Iterator<T> inner;
    private final Queue<T> lookahead = new ArrayDeque<>();
    private boolean isLookingAhead = false;
    
    public LookaheadIterator(Iterator<T> iterator) {
        inner = iterator;
    }

    @Override
    public boolean hasNext() {
        if (isLookingAhead) {
            return inner.hasNext();
        }
        return !lookahead.isEmpty() || inner.hasNext();
    }

    @Override
    public T next() {
        if (isLookingAhead) {
            final var value = inner.next();
            lookahead.add(value);
            return value;
        }
        final var value = lookahead.poll();
        if (value == null) {
            return inner.next();
        }
        return value;
    }

    public void mark() {
        isLookingAhead = true;
    }

    public void reset() {
        isLookingAhead = false;
    }

    public List<T> getLookahead() {
        // Copy to avoid returning something that could be invalidated later on.
        return new ArrayList<>(lookahead);
    }
}
