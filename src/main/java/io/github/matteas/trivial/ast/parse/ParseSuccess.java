package io.github.matteas.trivial.ast.parse;

import java.util.function.Function;
import io.github.matteas.trivial.ast.Ast;

class ParseSuccess implements ParseResult {
    public final Ast ast;
    public final Tokens remainingTokens;
    public ParseSuccess(Ast ast, Tokens remainingTokens) {
        this.ast = ast;
        this.remainingTokens = remainingTokens;
    }
    public <T> T match(Function<ParseSuccess, T> success, Function<ParseFailure, T> failure) {
        return success.apply(this);
    }
    public ParseResult withContext(ParseContext context) {
        return this;
    }
    public String toString() {
        return ast.toString();
    }
}