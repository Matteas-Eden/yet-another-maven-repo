package io.github.matteas.trivial;

import java.util.Map;
import java.util.HashMap;
import io.github.matteas.trivial.combinator.Combinator;
import static io.github.matteas.trivial.combinator.Primitives.PRIMITIVES;
import io.github.matteas.trivial.combinator.EvalError;
import static io.github.matteas.trivial.ast.parse.Parse.parse;
import static io.github.matteas.trivial.StandardLibrary.STANDARD_LIBRARY;

public class Repl {
    private Map<String, Combinator> scope = new HashMap<>(PRIMITIVES);

    public Repl() {
        eval(STANDARD_LIBRARY);
    }

    public void eval(String input) {
        // System.out.println("Input: " + input);
        final var lines = input.split(";");
        for (final var line : lines) {
            // System.out.println("Line: " + line);
            final var aliasParts = line.split("=");
            if (aliasParts.length > 2) {
                System.out.println("Syntax error");
                return;
            } else if (aliasParts.length == 2) {
                final var aliasName = aliasParts[0].trim();
                final var aliasExpression = parse(aliasParts[1]);
                // System.out.println("Adding alias " + aliasName);
                try {
                    final var aliasCombinator = aliasExpression.eval(scope);
                    scope.put(aliasName, aliasCombinator);
                } catch (EvalError error) {
                    System.out.println(error);
                    return;
                }
            } else {
                final var expression = parse(aliasParts[0]);
                try {
                    final var result = expression.eval(scope);
                    // System.out.println("Eval: " + result.toString());
                } catch (EvalError error) {
                    System.out.println(error);
                    return;
                }
            }
        }
    }
}
