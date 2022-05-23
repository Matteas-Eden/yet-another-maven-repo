package io.github.matteas.nontrivial.parser;

import java.util.Optional;
import java.util.Iterator;

public class Parser<
    V extends Value<V>,
    K extends TokenKind,
    T extends Token<V, K>
> {
    public Focus<V, K> parse(Iterator<T> tokens, Focus<V, K> focus) {
        while (tokens.hasNext()) {
            final var nextFocus = next(tokens.next(), focus);
            if (!nextFocus.isPresent()) {
                break;
            }
            focus = nextFocus.get();
        }
        return focus;
    }
    
    public Optional<Focus<V, K>> next(T token, Focus<V, K> focus) {
        return focus
            .refocusToNext(token.kind())
            .map(nextFocus -> nextFocus.withValue(token.value()));
    }
}