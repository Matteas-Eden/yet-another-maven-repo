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
    
    @Override
    public <T> T match(Function<ParseSuccess, T> success, Function<ParseFailure, T> failure) {
        return success.apply(this);
    }
    
    @Override
    public ParseResult withContext(ParseContext context) {
        return this;
    }
    
    @Override
    public String toString() {
        return ast.toString();
    }
}
