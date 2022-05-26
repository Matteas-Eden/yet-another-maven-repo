package io.github.matteas.nontrivial.lexer;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LexerRule<C, T> {
    public final RegularExpression<C> expression;
    public final Completer<C, T> completer;

    public LexerRule(RegularExpression<C> expression, Completer<C, T> completer) {
        this.expression = expression;
        this.completer = completer;
    }

    @FunctionalInterface
    public interface Completer<C, T> {
        T complete(List<C> contents);
    }

    public State initialState() {
        return new State(expression);
    }

    public class State {
        public final Focus<C> focus;
        public final Completer<C, T> completer = LexerRule.this.completer;
        public final boolean canComplete;
        public final boolean canAcceptCharacter;

        public State(RegularExpression<C> expression) {
            this(new Focus<>(expression));
        }

        private State(Focus<C> focus) {
            this.focus = focus;
            canComplete = focus.canComplete;
            canAcceptCharacter = focus.canAcceptCharacter;
        }

        public State next(C character) {
            return new State(focus.next(character));
        }

        @Override
        public int hashCode() {
            return Objects.hash(focus, completer);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherState = (State)other;
            return focus.equals(otherState.focus)
                && completer.equals(otherState.completer);
        }
    }
}
