package io.github.matteas.nontrivial.lexer;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

//public class Automaton<C, T> {
//    private final Map<List<Rule<C, T>>, State> memoized = new HashMap<>();
//    
//    public State getState(List<Rule<C, T>> rules) {
//        if (memoized.containsKey(rules)) {
//            return memoized.get(rules);
//        }
//        
//        final var ruleStates = rules
//            .stream()
//            .map(Rule::state)
//            .collect(Collectors.toList());
//        
//        final var state = new State(ruleStates);
//        memoized.put(rules, state);
//        return state;
//    }

    public class AutomatonState<C, T> {
        private final Map<C, AutomatonState<C, T>> pastTransitions = new HashMap<>();
        public final List<Rule<C, T>.State> ruleStates;
        public final boolean hasNext;
        public final Optional<Rule.Completer<C, T>> completer;

        public AutomatonState(List<Rule<C, T>.State> ruleStates) {
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

        public AutomatonState<C, T> next(C character) {
            if (pastTransitions.containsKey(character)) {
                return pastTransitions.get(character);
            }
            
            final var nextRuleStates = this.ruleStates
                .stream()
                .map(ruleState -> ruleState.next(character))
                .collect(Collectors.toList());
            
            final var nextState = new AutomatonState<C, T>(nextRuleStates);
            pastTransitions.put(character, nextState);
            return nextState;
        }
    }
//}
