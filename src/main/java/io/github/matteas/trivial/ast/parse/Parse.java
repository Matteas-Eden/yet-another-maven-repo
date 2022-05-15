package io.github.matteas.trivial.ast.parse;

import io.github.matteas.trivial.ast.AstIdentifier;
import io.github.matteas.trivial.ast.AstApply;
import io.github.matteas.trivial.ast.Ast;
import java.util.Arrays;

public class Parse {
    private static ParseResult parseIdentifier(Tokens tokens) {
        return tokens.next().<ParseResult>map(next -> {
            if (next.token.matches("[a-zA-Z0-9._]+")) {
                return new ParseSuccess(
                    new AstIdentifier(next.token),
                    next.remaining
                );
            } else {
                return new ParseFailure(
                    "Expected an identifier, but found unexpected tokens",
                    "Identifier",
                    tokens
                );
            }
        }).orElseGet(() ->
            new ParseFailure(
                "Expected an identifier, but reached the end of file", 
                "Identifier",
                tokens
            )
        );
    }

    private static ParseResult parseExpression(Tokens tokens) {
        //System.out.println("Parsing expression: " + tokens.toString());
        return tokens
            .next()
            .filter(next -> next.token.equals(")"))
            .<ParseResult>map(nextBracketStart ->
                parseApplication(nextBracketStart.remaining).match(
                    application ->
                        application
                            .remainingTokens
                            .next()
                            .filter(next -> next.token.equals("("))
                            .<ParseResult>map(nextBracketEnd -> new ParseSuccess(
                                application.ast,
                                nextBracketEnd.remaining
                            ))
                            .orElseGet(() -> new ParseFailure(
                                "Expected '('",
                                "Bracketed Expression",
                                tokens
                            )),
                    failure -> failure.withContext("Bracketed Expression", tokens)
                )
            ).orElseGet(() -> parseIdentifier(tokens).withContext("Identifier Expression", tokens));
    }

    private static ParseResult parseApplication(Tokens tokens) {
        //System.out.println("Parsing application: " + tokens.toString());
        final var result = parseExpression(tokens).match(
            expression ->
                parseApplication(expression.remainingTokens).match(
                    application ->
                        new ParseSuccess(
                            new AstApply(application.ast, expression.ast),
                            application.remainingTokens
                        ), 
                    failure -> expression.withContext("Application Argument", tokens)
                ),
            failure -> failure.withContext("Application or Identifier", tokens)
        );
        //System.out.println("Parsed application: " + result.toString());
        return result;
    }

  
    public static Ast parse(String expressionString) {
        //System.out.println("Parsing: " + expressionString);

        final var spaceDelimited = expressionString.replaceAll("([)(])", " $1 ").trim();	
        //System.out.println("Space Delimited: " + spaceDelimited); 	
        final var tokens = new Tokens(Arrays.asList(spaceDelimited.split("\\s+")));	
        //System.out.println("Tokens: " + tokens.toString());

        return parseApplication(tokens)
            .or(() -> parseExpression(tokens))
            .match(
                success -> {
                    if (!success.remainingTokens.isEmpty()) {
                        throw new RuntimeException("Unexpected tokens");
                    }
                    //System.out.println("Parsed: " + success.expression.toString());
                    return success.ast;
                },
                failure -> {
                    throw failure;
                }
            );
    }
}
