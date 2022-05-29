package io.github.matteas.nontrivial.parser;

import java.util.function.UnaryOperator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Also known as a Huet zipper, the derivative of the type {@link Syntax}, or the one-hole context of {@link Syntax}.
 */
public class Focus<V extends Value<V>, K extends @NonNull Object> {
    /**
     * The current focal point in the syntax tree.
     */
    public final ValidSyntax<V, K> syntax;

    /**
     * A linked list of contexts to reach the rest of the syntax tree
     * that is outside the focus. This is effectively the parent of
     * the focal point {@link syntax}, but with the parent-child arrows
     * of the tree drawn the opposite direction.
     */
    public final Context<V, K> context;
    
    public Focus(ValidSyntax<V, K> syntax) {
        this(syntax, new Context.Root<>());
    }

    public Focus(ValidSyntax<V, K> syntax, Context<V, K> context) {
        this.syntax = syntax;
        this.context = context;
    }

    public Set<K> acceptableKinds() {
        if (context.isRoot()) {
            return syntax.acceptableKinds;
        }
        
        return Stream.concat(
            syntax.acceptableKinds.stream(),
            syntax.canComplete
                .map(
                    value -> context
                        .unfocusToNextSyntax(value)
                        .acceptableKinds()
                        .stream()
                )
                .orElseGet(Stream::empty)
        ).collect(Collectors.toSet());
    }

    public Optional<V> canComplete() {
        if (context.isRoot()) {
            return syntax.canComplete;
        }
        
        return syntax.canComplete
            .flatMap(
                value -> context
                    .unfocusToNextSyntax(value)
                    .canComplete()
            );
    }

    /**
     * Tries moving the focus to the next Element syntax node that accepts the given token kind.
     */
    public Optional<Focus<V, K>> refocusToNext(K kind) {
        return unfocusToNext(kind).map(focus -> focus.focusTo(kind));
    }

    /**
     * Also known in literature as "locate".
     * Moves up the focus until a syntax that accepts the given token kind is found.
     */
    private Optional<Focus<V, K>> unfocusToNext(K kind) {
        if (syntax.accepts(kind)) {
            return Optional.of(this);
        } else if (context.isRoot()) {
            return Optional.empty();
        }
        return syntax.canComplete
            .flatMap(v -> context.unfocusToNextSyntax(v).unfocusToNext(kind));
    }

    /**
     * Also known in literature as "pierce".
     * Moves down the focus to the Element syntax node that accepts the given token kind.
     */
    private Focus<V, K> focusTo(K kind) {
        assert syntax.accepts(kind);
        return syntax.focus(kind, context);
    }

    /**
     * Replaces the current syntax node with one that represents the given value.
     */
    public Focus<V, K> withValue(V value) {
        return new Focus<>(new ValidSyntax.Success<>(value), context);
    }

    public interface Context<V extends Value<V>, K extends @NonNull Object> {
        /**
         * Also known in literature as "plug"
         */
        Focus<V, K> unfocusToNextSyntax(V v);

        boolean isRoot();
        
        public static class FollowBy<V extends Value<V>, K extends @NonNull Object> implements Context<V, K> {
            public final ValidSyntax<V, K> syntax;
            public final Context<V, K> next;
            
            public FollowBy(ValidSyntax<V, K> syntax, Context<V, K> next) {
                this.syntax = syntax;
                this.next = next;
            }
    
            @Override
            public Focus<V, K> unfocusToNextSyntax(V v) {
                // Found the next syntax, so we stop unfocusing here.
                return new Focus<>(syntax, new Prepend<>(v, next));
            }

            @Override
            public boolean isRoot() {
                return false;
            }
        }
        
        public static class Prepend<V extends Value<V>, K extends @NonNull Object> implements Context<V, K> {
            public final V value;
            public final Context<V, K> next;
            public Prepend(V value, Context<V, K> next) {
                this.value = value;
                this.next = next;
            }
            
            @Override
            public Focus<V, K> unfocusToNextSyntax(V v) {
                return next.unfocusToNextSyntax(v.prepend(value));
            }

            @Override
            public boolean isRoot() {
                return false;
            }
        }
        
        public static class Apply<V extends Value<V>, K extends @NonNull Object> implements Context<V, K> {
            public final UnaryOperator<V> mapper;
            public final Context<V, K> next;
            public Apply(UnaryOperator<V> mapper, Context<V, K> next) {
                this.mapper = mapper;
                this.next = next;
            }
            
            @Override
            public Focus<V, K> unfocusToNextSyntax(V v) {
                return next.unfocusToNextSyntax(mapper.apply(v));
            }

            @Override
            public boolean isRoot() {
                return false;
            }
        }
        
        public static class Root<V extends Value<V>, K extends @NonNull Object> implements Context<V, K> {
            @Override
            public Focus<V, K> unfocusToNextSyntax(V v) {
                return new Focus<>(new ValidSyntax.Success<>(v), this);
            }

            @Override
            public boolean isRoot() {
                return true;
            }
        }
    }
}