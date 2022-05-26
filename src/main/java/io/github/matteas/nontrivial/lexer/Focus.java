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
        this(new Context<>(expression));
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
    
    public static class Context<C> {
        public final RegularExpression<C> nextExpression;
        public final Optional<Context<C>> context;
        public final boolean canComplete;
        public final boolean canAcceptCharacter;
        public final boolean canAcceptWord;
        
        public Context(RegularExpression<C> nextExpression) {
            this.nextExpression = nextExpression;
            context = Optional.empty();
            canComplete = nextExpression.canComplete;
            canAcceptCharacter = nextExpression.canAcceptCharacter;
            canAcceptWord = nextExpression.canAcceptWord;
        }

        public Context(RegularExpression<C> nextExpression, Context<C> context) {
            this.nextExpression = nextExpression;
            this.context = Optional.of(context);
            canComplete = nextExpression.canComplete && context.canComplete;
            canAcceptCharacter = nextExpression.canAcceptCharacter || context.canAcceptCharacter;
            canAcceptWord = nextExpression.canAcceptWord && context.canAcceptWord;
        }

        public Context<C> prepend(RegularExpression<C> nextExpression) {
            return new Context<>(nextExpression, this);
        }

        public Focus<C> unfocus(C character) {
            final var nextFocus = nextExpression.focus(character, context.get());
            if (nextExpression.canComplete && context.isPresent()) {
                return nextFocus.union(context.get().unfocus(character));
            }
            return nextFocus;
        }

        @Override
        public int hashCode() {
            return Objects.hash(context, nextExpression);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherContext = (Context<C>)other;
            return nextExpression.equals(otherContext.nextExpression)
                && context.equals(otherContext.context);
        }
    }
}
