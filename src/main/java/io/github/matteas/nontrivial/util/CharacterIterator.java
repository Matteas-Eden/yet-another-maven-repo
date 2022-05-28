package io.github.matteas.nontrivial.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharacterIterator implements Iterator<Character> {
    public final String string;
    private int position = 0;

    public CharacterIterator(String string) {
        this.string = string;
    }

    @Override
    public boolean hasNext() {
        return position < string.length();
    }

    @Override
    public Character next() {
        try {
            final var nextCharacter = string.charAt(position);
            position++;
            return nextCharacter;
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException("No more characters to iterate", e);
        }
    }
}