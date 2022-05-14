package io.github.matteas.trivial.ast.parse;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ParseResult {
    <T> T match(Function<ParseSuccess, T> success, Function<ParseFailure, T> failure);

    ParseResult withContext(ParseContext context);

    default ParseResult withContext(String grammarRule, Tokens tokens) {
        return withContext(new ParseContext(grammarRule, tokens));
    }

    default ParseResult or(Supplier<ParseResult> other) {
        return match(success -> success, failure -> other.get());
    }
}
