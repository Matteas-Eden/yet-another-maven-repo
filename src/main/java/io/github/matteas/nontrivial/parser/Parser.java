package io.github.matteas.nontrivial.parser;

public class Parser {
    public static Focus parse(Iterator<Token> tokens, Focus focus) {
        while (tokens.hasNext()) {
            final var nextFocus = next(tokens.next(), focus);
            if (!nextFocus.isPresent()) {
                break;
            }
            focus = nextFocus.get();
        }
        return focus;
    }
    
    public static Optional<Focus> next(Token token, Focus focus) {
        return focus
            .refocusToNext(token.kind())
            .map(nextFocus -> nextFocus.withValue(token));
    }
}