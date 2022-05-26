package io.github.matteas.nontrivial.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.Optional;

import org.checkerframework.checker.initialization.qual.UnknownInitialization;

/**
 * A cell in a Propagation Network used to find the steady state value.
 * Each InductiveProperty reacts to changes of its dependencies, and the
 * network is calculated bottom-up.
 */
public interface InductiveProperty<T> {
    T get();
    void dependedBy(@UnknownInitialization InductiveProperty<?> dependent);
    void update();

    public class Constant<T> implements InductiveProperty<T> {
        private final T value;
        
        public Constant(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void dependedBy(@UnknownInitialization InductiveProperty<?> dependent) {
            // Nothing to do.
        }

        @Override
        public void update() {
            // Nothing to do.
        }
    }
    
    public class Rule<T> implements InductiveProperty<T> {
        private final List<InductiveProperty<?>> listeners = new ArrayList<>();
        private final Supplier<T> rule;
        private T value;
        
        public Rule(Iterable<InductiveProperty<?>> dependencies, Supplier<T> rule) {
            for (final var dependency : dependencies) {
                dependency.dependedBy(this);
            }
            this.rule = rule;
            update();
        }

        @Override
        public void dependedBy(@UnknownInitialization InductiveProperty<?> dependent) {
            listeners.add(dependent);
        }

        @Override
        public void update() {
            final var newValue = rule.get();
            if (!newValue.equals(value)) {
                value = newValue;
                for (final var listener : listeners) {
                    listener.update();
                }
            }
        }

        @Override
        public T get() {
            return value;
        }
    }
    
    public class Deferred<T> implements InductiveProperty<T> {
        private final List<InductiveProperty<?>> listeners = new ArrayList<>();
        private final T defaultValue;
        private Optional<InductiveProperty<T>> realized = Optional.empty();

        public Deferred(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void realize(InductiveProperty<T> realization) {
            realization.dependedBy(this);
            realized = Optional.of(realization);
            update();
        }
        
        @Override
        public void dependedBy(@UnknownInitialization InductiveProperty<?> dependent) {
            listeners.add(dependent);
        }

        @Override
        public void update() {
            for (final var listener : listeners) {
                listener.update();
            }
        }

        @Override
        public T get() {
            return realized.map(InductiveProperty::get).orElse(defaultValue);
        }
    }
}
