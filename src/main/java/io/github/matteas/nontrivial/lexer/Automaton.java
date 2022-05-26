package io.github.matteas.nontrivial.lexer;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.checker.nullness.qual.UnderInitialization;

public class Automaton<C, T> {
    private final Map<List<LexerRule<C, T>.State>, State> memoized = new HashMap<>();
    public final State initialState;

    public Automaton(List<LexerRule<C, T>> rules) {
        final var ruleStates = rules
            .stream()
            .map(LexerRule::initialState)
            .collect(Collectors.toList());

        initialState = getState(ruleStates);
    }

    @RequiresNonNull("memoized")
    public State getState(
        @UnderInitialization(Automaton<C, T>.class) Automaton<C, T> this, 
        List<LexerRule<C, T>.State> ruleStates
    ) {
        if (memoized.containsKey(ruleStates)) {
            return memoized.get(ruleStates);
        }
        
        final var state = new State(ruleStates);
        memoized.put(ruleStates, state);
        return state;
    }

    public class State {
        private final Map<C, State> pastTransitions = new HashMap<>();
        public final List<LexerRule<C, T>.State> ruleStates;
        public final boolean hasNext;
        public final Optional<LexerRule.Completer<C, T>> completer;

        public State(List<LexerRule<C, T>.State> ruleStates) {
            this.ruleStates = ruleStates;
            
            this.hasNext = ruleStates
                .stream()
                .anyMatch(ruleState -> ruleState.canAcceptCharacter);

            // Rules at the front of the list have higher priority than does at the back
            // in the case we need to tie-break.
            this.completer = ruleStates
                .stream()
                .filter(ruleState -> ruleState.canComplete)
                .findFirst()
                .map(ruleState -> ruleState.completer);
        }

        public State next(C character) {
            if (pastTransitions.containsKey(character)) {
                return pastTransitions.get(character);
            }
            
            final var nextRuleStates = this.ruleStates
                .stream()
                .map(ruleState -> ruleState.next(character))
                .collect(Collectors.toList());
            
            final var nextState = getState(nextRuleStates);
            pastTransitions.put(character, nextState);
            return nextState;
        }
    }
}
