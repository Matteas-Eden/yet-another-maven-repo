package io.github.matteas.nontrivial.lexer;

import java.util.Set;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class Focus<C> {
    public final Set<Context<C>> contexts;
    public final boolean canComplete;
    public final boolean canAcceptCharacter;

    public Focus() {
        this(Collections.emptySet());
    }

    public Focus(RegularExpression<C> expression) {
        this(new Context.Sequence<>(expression, new Context.Root<>()));
    }
    
    public Focus(Context<C> context) {
        this(Set.of(context));
    }

    public Focus(Set<Context<C>> contexts) {
        this.contexts = contexts;
        canComplete = contexts
            .stream()
            .anyMatch(context -> context.canComplete);
        canAcceptCharacter = contexts
            .stream()
            .anyMatch(context -> context.canAcceptCharacter && context.canAcceptWord);
    }

    public Focus<C> union(Focus<C> otherFocus) {
        return new Focus<>(
            Stream.concat(
                contexts.stream(),
                otherFocus.contexts.stream()
            ).collect(Collectors.toSet())
        );
    }

    public Focus<C> next(C character) {
        return new Focus<>(
            contexts
                .stream()
                .flatMap(context -> context.unfocus(character).contexts.stream())
                .collect(Collectors.toSet())
        );
    }

    @Override
    public int hashCode() {
        return contexts.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != getClass()) {
            return false;
        }
        final var otherFocus = (Focus<C>)other;
        return contexts.equals(otherFocus.contexts);
    }
    
    public static abstract class Context<C> {
        public final boolean canComplete;
        public final boolean canAcceptCharacter;
        public final boolean canAcceptWord;

        public Context(
            boolean canComplete,
            boolean canAcceptCharacter,
            boolean canAcceptWord
        ) {
            this.canComplete = canComplete;
            this.canAcceptCharacter = canAcceptCharacter;
            this.canAcceptWord = canAcceptWord;
        }
        
        public final Context<C> prepend(RegularExpression<C> nextExpression) {
            return new Sequence<>(nextExpression, this);
        }

        public abstract Focus<C> unfocus(C character);

        public static class Sequence<C> extends Context<C> {
            public final RegularExpression<C> expression;
            public final Context<C> next;
    
            public Sequence(RegularExpression<C> expression, Context<C> next) {
                super(
                    expression.canComplete && next.canComplete,
                    expression.canAcceptCharacter || next.canAcceptCharacter,
                    expression.canAcceptWord && next.canAcceptWord
                );
                this.expression = expression;
                this.next = next;
            }

            @Override
            public Focus<C> unfocus(C character) {
                final var nextFocus = expression.focus(character, next);
                if (expression.canComplete) {
                    return nextFocus.union(next.unfocus(character));
                }
                return nextFocus;
            }

            @Override
            public int hashCode() {
                final var SEQUENCE_TAG = 3;
                return Objects.hash(expression, next);
            }
    
            @Override
            public boolean equals(@Nullable Object other) {
                if (other == null) {
                    return false;
                }
                if (other.getClass() != getClass()) {
                    return false;
                }
                final var otherContext = (Sequence<C>)other;
                return expression.equals(otherContext.expression)
                    && next.equals(otherContext.next);
            }
        }

        public static class Root<C> extends Context<C> {
            public Root() {
                super(true, false, true);
            }

            @Override
            public Focus<C> unfocus(C character) {
                return new Focus<>();
            }

            @Override
            public int hashCode() {
                final var ROOT_TAG = 7;
                return Objects.hash(ROOT_TAG);
            }
    
            @Override
            public boolean equals(@Nullable Object other) {
                if (other == null) {
                    return false;
                }
                return other.getClass() == getClass();
            }
        }
    }
}
