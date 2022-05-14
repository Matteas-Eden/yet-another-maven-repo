package io.github.matteas.trivial.ast.parse;

import java.util.List;
import java.util.Optional;

public class Tokens {
    public static class Next {
        public final String token;
        public final Tokens remaining;
        public Next(String token, Tokens remaining) {
            this.token = token;
            this.remaining = remaining;
        }
    }
    
    private final List<String> tokens;
    
    public Tokens(List<String> tokens) {
        this.tokens = tokens;
    }
    
    public boolean isEmpty() {
        return tokens.isEmpty();
    }
    
    public Optional<Next> next() {
        if (tokens.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new Next(
                tokens.get(tokens.size() - 1),
                new Tokens(tokens.subList(0, tokens.size() - 1))
            ));
        }
    }
    
    public String toString() {
        return String.join(",", tokens);
    }
}
