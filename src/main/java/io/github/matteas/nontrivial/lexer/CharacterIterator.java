package io.github.matteas.nontrivial.lexer;

import java.util.Iterator;

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
        final var nextCharacter = string.charAt(position);
        position++;
        return nextCharacter;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}