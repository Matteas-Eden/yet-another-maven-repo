package io.github.matteas.nontrivial.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.NoSuchElementException;
import java.util.List;

class LookaheadIteratorTest {
    @Test
    void givenNotMarkedWhenResetThenThrowException() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3).iterator());

        // When, Then
        assertThrows(LookaheadIterator.NotMarkedException.class, () -> iter.reset());
    }

    @Test
    void givenMarkedWhenResetThenReturnToPreviousMark() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        iter.mark();
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());

        // When
        iter.reset();

        // Then
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertEquals(4, iter.next());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }
    
    @Test
    void givenMarkedTwiceWhenResetThenReturnToLastMark() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        iter.mark();
        assertEquals(2, iter.next());
        iter.mark();
        assertEquals(3, iter.next());

        // When
        iter.reset();

        // Then
        assertEquals(3, iter.next());
        assertEquals(4, iter.next());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }
    

    @Test
    void givenNotMarkedThenGetLookaheadReturnsNothing() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());

        // Then
        assertEquals(List.of(), iter.getLookahead());
    }

    @Test
    void givenMarkedAndNextThenGetLookaheadReturnsItemsAfterMark() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        iter.mark();
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());

        // Then
        assertEquals(List.of(2, 3), iter.getLookahead());
    }

    @Test
    void givenMarkedWithoutNextThenGetLookaheadReturnsNothing() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        iter.mark();

        // Then
        assertEquals(List.of(), iter.getLookahead());
    }

    @Test
    void givenMarkResettedThenGetLookaheadReturnsNothing() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        assertEquals(1, iter.next());
        iter.mark();
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        iter.reset();

        // Then
        assertEquals(List.of(), iter.getLookahead());
    }

    @Test
    void whenMarkDuringPreviouslyVisitedItemThenContinueIteratingVisitedItems() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        iter.mark();
        assertEquals(1, iter.next());
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        iter.reset();
        assertEquals(1, iter.next());

        // When
        iter.mark();

        // Then
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertEquals(4, iter.next());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    @Test
    void whenMarkAndResetOverPreviouslyVisitedItemThenReturnToMarkedItem() {
        // Given
        final var iter = new LookaheadIterator<Integer>(List.of(1, 2, 3, 4).iterator());
        iter.mark();
        assertEquals(1, iter.next());
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        iter.reset();
        assertEquals(1, iter.next());
        iter.mark();
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertEquals(4, iter.next());

        // When
        iter.reset();

        // Then
        assertEquals(2, iter.next());
        assertEquals(3, iter.next());
        assertEquals(4, iter.next());
        assertFalse(iter.hasNext());
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }
}