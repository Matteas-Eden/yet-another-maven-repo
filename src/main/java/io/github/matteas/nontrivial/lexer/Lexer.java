package io.github.matteas.nontrivial.lexer;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class Lexer<C, T> {
    public final Automaton<C, T> automaton;
    
    public Lexer(List<Rule<C, T>> rules) {
        automaton = new Automaton<>(rules);
    }
    
    public Iterator<T> tokenize(Iterator<C> input) {
        final var iterator = new LookaheadIterator(input);
        final List<T> tokens = new ArrayList<>();
        
        while (iterator.hasNext()) {
            // Start of a new token, so start lexing from the initial state.
            var state = automaton.initialState;

            // Let the iterator know that we might want to backtrack back to this point later.
            iterator.mark();
            
            Optional<Rule.Completer<C, T>> completer = Optional.empty();
            
            while (iterator.hasNext() && state.hasNext) {
                state = state.next(iterator.next());

                // Longest prefix match wins.
                if (state.completer.isPresent()) {
                    completer = state.completer;
                }
            }

            if (completer.isPresent()) {
                tokens.add(completer.complete(iterator.getLookahead()));
            } else {
                // Failed to find any matching tokens, so backtrack and skip over to the next token.
                iterator.reset();
                iterator.next();
            }
        }

        return tokens;
    }
}
