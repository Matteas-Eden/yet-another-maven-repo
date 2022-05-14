package io.github.matteas.trivial.ast.parse;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ParseFailure extends RuntimeException implements ParseResult {
    public final String reason;
    public final List<ParseContext> context;

    public ParseFailure(String reason, String grammarRule, Tokens tokens) {
        this(reason, new ParseContext(grammarRule, tokens));
    }

    public ParseFailure(String reason, ParseContext context) {
        super("Syntax Error: " + reason + "\n" + "While parsing \n\t" + context.toString());
        this.reason = reason;
        this.context = List.of(context);
    }

    public ParseFailure(ParseFailure failure, ParseContext moreContext) {
        super("Syntax Error: " + failure.reason + "\nWhile parsing\n\t"
                + String.join("\n\t", 
                        failure
                            .context
                            .stream()
                            .map(ParseContext::toString)
                            .collect(Collectors.toList())
                    )
                + "\n\t" + moreContext.toString()
             );
        this.reason = failure.reason;
        this.context = Stream.concat(failure.context.stream(), Stream.of(moreContext))
                .collect(Collectors.toUnmodifiableList());
    }

    public <T> T match(Function<ParseSuccess, T> success, Function<ParseFailure, T> failure) {
        return failure.apply(this);
    }

    public ParseResult withContext(ParseContext context) {
        return new ParseFailure(this, context);
    }
}
