package io.github.matteas.trivial.ast.parse;

class ParseContext {
    public final String grammarRule;
    public final Tokens tokens;
    
    public ParseContext(String grammarRule, Tokens tokens) {
        this.grammarRule = grammarRule;
        this.tokens = tokens;
    }
    
    public String toString() {
        return grammarRule + " with tokens: " + tokens.toString();
    }
}
