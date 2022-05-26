package io.github.matteas.nontrivial.lexer;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class RegularExpression<C> {
    /**
     * Also known in literature as isNullable
     */
    public final boolean canComplete;

    /**
     * Also know in literature as hasFirst
     */
    public final boolean canAcceptCharacter;

    /**
     * Also know in literature as isProductive
     */
    public final boolean canAcceptWord;
    
    public abstract Focus<C> focus(C character, Focus.Context<C> context);

    protected RegularExpression(boolean canComplete, boolean canAcceptCharacter) {
        this.canComplete = canComplete;
        this.canAcceptCharacter = canAcceptCharacter;
        canAcceptWord = canComplete || canAcceptCharacter;
    }

    public static class Empty<C> extends RegularExpression<C> {
        public Empty() {
            super(true, false);
        }

        @Override
        public Focus<C> focus(C character, Focus.Context<C> context) {
            return new Focus<>();
        }
        
        @Override
        public int hashCode() {
            final var EMPTY_TAG = 11;
            return Objects.hash(EMPTY_TAG);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            return true;
        }
    }
    
    public static class Character<C> extends RegularExpression<C> {
        public final C character;
        
        public Character(C character) {
            super(false, true);
            
            this.character = character;
        }

        @Override
        public Focus<C> focus(C character, Focus.Context<C> context) {
            if (this.character.equals(character)) {
                return new Focus<>(context);
            }
            return new Focus<>();
        }
        
        @Override
        public int hashCode() {
            final var CHARACTER_TAG = 13;
            return Objects.hash(CHARACTER_TAG, character);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherCharacter = (Character<C>)other;
            return character.equals(otherCharacter.character);
        }
    }

    public static class Disjunction<C> extends RegularExpression<C> {
        public final RegularExpression<C> left;
        public final RegularExpression<C> right;

        public Disjunction(RegularExpression<C> left, RegularExpression<C> right) {
            super(
                left.canComplete || right.canComplete,
                left.canAcceptCharacter || right.canAcceptCharacter
            );
            
            this.left = left;
            this.right = right;
        }

        @Override
        public Focus<C> focus(C character, Focus.Context<C> context) {
            return left.focus(character, context).union(right.focus(character, context));
        }
        
        @Override
        public int hashCode() {
            final var DISJUNCTION_TAG = 17;
            return Objects.hash(DISJUNCTION_TAG, left, right);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherDisjunction = (Disjunction<C>)other;
            return left.equals(otherDisjunction.left)
                && right.equals(otherDisjunction.right);
        }
    }

    public static class Sequence<C> extends RegularExpression<C> {
        public final RegularExpression<C> left;
        public final RegularExpression<C> right;

        public Sequence(RegularExpression<C> left, RegularExpression<C> right) {
            super(
                left.canComplete && right.canComplete,
                (left.canAcceptCharacter && (right.canComplete || right.canAcceptCharacter))
                    || (left.canComplete && right.canAcceptCharacter)
            );
            
            this.left = left;
            this.right = right;
        }

        @Override
        public Focus<C> focus(C character, Focus.Context<C> context) {
            final var leftFocus = left.focus(character, context.prepend(right));
            if (left.canComplete) {
                return leftFocus.union(right.focus(character, context));
            }
            return leftFocus;
        }
        
        @Override
        public int hashCode() {
            final var SEQUENCE_TAG = 23;
            return Objects.hash(SEQUENCE_TAG, left, right);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherSequence = (Sequence<C>)other;
            return left.equals(otherSequence.left)
                && right.equals(otherSequence.right);
        }
    }

    public static class Repetition<C> extends RegularExpression<C> {
        public final RegularExpression<C> inner;

        public Repetition(RegularExpression<C> inner) {
            super(true, inner.canAcceptCharacter);
            
            this.inner = inner;
        }

        @Override
        public Focus<C> focus(C character, Focus.Context<C> context) {
            return inner.focus(character, context.prepend(this));
        }
        
        @Override
        public int hashCode() {
            final var REPETITION_TAG = 29;
            return Objects.hash(REPETITION_TAG, inner);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (other == null) {
                return false;
            }
            if (other.getClass() != getClass()) {
                return false;
            }
            final var otherRepetition = (Repetition<C>)other;
            return inner.equals(otherRepetition.inner);
        }
    }
}