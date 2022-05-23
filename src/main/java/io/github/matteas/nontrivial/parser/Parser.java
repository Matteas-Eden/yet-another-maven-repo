package io.github.matteas.nontrivial.parser;

import java.util.Optional;
import java.util.Iterator;

public class Parser<V extends Value<V>, K extends TokenKind> {
    public Focus<V, K> parse(Iterator<Token<V, K>> tokens, Focus<V, K> focus) {
        while (tokens.hasNext()) {
            final var nextFocus = next(tokens.next(), focus);
            if (!nextFocus.isPresent()) {
                break;
            }
            focus = nextFocus.get();
        }
        return focus;
    }
    
    public Optional<Focus<V, K>> next(Token<V, K> token, Focus<V, K> focus) {
        return focus
            .refocusToNext(token.kind())
            .map(nextFocus -> nextFocus.withValue(token));
    }
}