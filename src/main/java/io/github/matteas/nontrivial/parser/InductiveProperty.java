package io.github.matteas.nontrivial.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.Optional;
import java.util.Objects;

import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A cell in a Propagation Network used to find the steady state value.
 * Each InductiveProperty reacts to changes of its dependencies, and the
 * network is calculated bottom-up.
 */
public interface InductiveProperty<T extends @NonNull Object> {
    T get();
    void dependedBy(InductiveProperty<?> dependent);
    void update();

    public static <T extends @NonNull Object> Constant<T> constant(T value) {
        return new Constant<>(value);
    }

    public static <T extends @NonNull Object> Rule<T> rule(Iterable<InductiveProperty<?>> dependencies, Supplier<T> calculation) {
        final var rule = new Rule<>(calculation);
        for (final var dependency : dependencies) {
            dependency.dependedBy(rule);
        }
        return rule;
    }

    public static <T extends @NonNull Object> Deferred<T> deferred(T defaultValue) {
        return new Deferred<>(defaultValue);
    }

    public class Constant<T extends @NonNull Object> implements InductiveProperty<T> {
        private final T value;
        
        private Constant(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void dependedBy(InductiveProperty<?> dependent) {
            // Nothing to do.
        }

        @Override
        public void update() {
            // Nothing to do.
        }
    }
    
    public class Rule<T extends @NonNull Object> implements InductiveProperty<T> {
        private final List<InductiveProperty<?>> listeners = new ArrayList<>();
        private final Supplier<T> calculation;
        private T value;
        
        private Rule(Supplier<T> calculation) {
            this.calculation = calculation;
            this.value = calculation.get();
        }

        @Override
        public void dependedBy(InductiveProperty<?> dependent) {    
            listeners.add(dependent);
        }

        @Override
        public void update() {
            final var newValue = calculation.get();
            if (!Objects.equals(newValue, value)) {
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
    
    public class Deferred<T extends @NonNull Object> implements InductiveProperty<T> {
        private final List<InductiveProperty<?>> listeners = new ArrayList<>();
        private final T defaultValue;
        private Optional<InductiveProperty<T>> realized = Optional.empty();

        private Deferred(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void realize(InductiveProperty<T> realization) {
            realization.dependedBy(this);
            realized = Optional.of(realization);
            update();
        }
        
        @Override
        public void dependedBy(InductiveProperty<?> dependent) {
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
            return realized
                .map(InductiveProperty::get)
                .orElse(defaultValue);
        }
    }
}
