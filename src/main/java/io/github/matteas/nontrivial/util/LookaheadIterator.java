package io.github.matteas.nontrivial.util;

import java.util.Iterator;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LookaheadIterator<T extends @NonNull Object> implements Iterator<T> {
    private final Iterator<T> inner;
    private Optional<Queue<T>> lookahead = Optional.empty();
    private Queue<T> backtrack = new ArrayDeque<>();
    
    public LookaheadIterator(Iterator<T> iterator) {
        inner = iterator;
    }

    @Override
    public boolean hasNext() {
        return inner.hasNext() || !backtrack.isEmpty();
    }

    @Override
    public T next() {
        final var value = Optional
            .ofNullable(backtrack.poll())
            .orElseGet(() -> inner.next());
        lookahead.map(queue -> queue.add(value));
        return value;
    }

    public void mark() {
        if (lookahead.isPresent()) {
            throw new AlreadyMarkedException();
        }

        lookahead = Optional.of(new ArrayDeque<>());
    }

    public void reset() {
        if (!lookahead.isPresent()) {
            throw new NotMarkedException();
        }

        final var queue = lookahead.get();
        lookahead = Optional.empty();
        queue.addAll(backtrack);
        backtrack = queue;
    }

    public List<T> getLookahead() {
        // Copy to avoid returning something that could be invalidated later on.
        return lookahead
            .<List<T>>map(queue -> new ArrayList<>(queue))
            .orElse(List.of());
    }

    public static abstract class Exception extends RuntimeException {
        public Exception(String message) {
            super(message);
        }
    }

    public static class AlreadyMarkedException extends Exception {
        public AlreadyMarkedException() {
            super("Cannot mark the lookahead iterator. Iterator is already marked");
        }
    }

    public static class NotMarkedException extends Exception {
        public NotMarkedException() {
            super("Cannot reset the lookahead iterator. Iterator doesn't have any mark to reset to");
        }
    }
}
