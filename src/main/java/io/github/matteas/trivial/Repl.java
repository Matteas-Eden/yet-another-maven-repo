package io.github.matteas.trivial;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import io.github.matteas.trivial.combinator.Combinator;
import static io.github.matteas.trivial.combinator.Primitives.PRIMITIVES;
import io.github.matteas.trivial.combinator.EvalError;
import static io.github.matteas.trivial.ast.parse.Parse.parse;
import static io.github.matteas.trivial.StandardLibrary.STANDARD_LIBRARY;

public class Repl {
    private Map<String, Combinator> scope = new HashMap<>(PRIMITIVES);

    public Repl() {
        try {
            eval(STANDARD_LIBRARY);
        } catch (Exception exception) {
            throw new RuntimeException("Error while evaluating the standard library", exception);
        }
    }

    public Optional<Combinator> eval(String input) throws Exception {
        // System.out.println("Input: " + input);
        final var statements = input.split(";");
        Optional<Combinator> finalValue = Optional.empty();
        for (final var statement : statements) {
            // System.out.println("statement: " + statement);
            final var aliasParts = statement.split("=");
            if (aliasParts.length > 2) {
                throw new Exception("Syntax error: At most one '=' per statement, but got: " + statement);
            } else if (aliasParts.length == 2) {
                final var aliasName = aliasParts[0].trim();
                final var aliasExpression = parse(aliasParts[1]);
                // System.out.println("Adding alias " + aliasName);
                try {
                    final var aliasCombinator = aliasExpression.eval(scope);
                    finalValue = Optional.of(aliasCombinator);
                    scope.put(aliasName, aliasCombinator);
                } catch (EvalError error) {
                    throw new Exception("Error while evaluating: " + statement, error);
                }
            } else {
                final var expression = parse(aliasParts[0]);
                try {
                    finalValue = Optional.of(expression.eval(scope));
                    // System.out.println("Eval: " + result.toString());
                } catch (EvalError error) {
                    throw new Exception("Error while evaluating: " + statement, error);
                }
            }
        }
        return finalValue;
    }
}
