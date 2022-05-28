package io.github.matteas.nontrivial.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.NoSuchElementException;

class CharacterIteratorTest {
    @Test
    void emptyStringHasNone() {
        assertFalse(new CharacterIterator("").hasNext());
    }
    
    @Test
    void emptyStringThrowsWhenIterating() {
        assertThrows(NoSuchElementException.class, () -> new CharacterIterator("").next());
    }
    
    @Test
    void singleCharacterStringHasNext() {
        assertTrue(new CharacterIterator("a").hasNext());
    }
    
    @Test
    void singleCharacterStringIteratesOnce() {
        assertEquals('a', new CharacterIterator("a").next());
    }
    
    @Test
    void multiCharacterStringIteratesThreeTimes() {
        final var iter = new CharacterIterator("abc");
        assertEquals('a', iter.next());
        assertEquals('b', iter.next());
        assertEquals('c', iter.next());
        assertFalse(iter.hasNext());
    }
    
    @Test
    void multiCharacterStringThrowsAfterEnd() {
        // Given
        final var iter = new CharacterIterator("abc");
        assertEquals('a', iter.next());
        assertEquals('b', iter.next());
        assertEquals('c', iter.next());
        assertFalse(iter.hasNext());

        // When, Then
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }
}
