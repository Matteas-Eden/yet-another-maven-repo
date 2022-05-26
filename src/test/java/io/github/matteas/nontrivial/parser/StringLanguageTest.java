package io.github.matteas.nontrivial.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.List;
import io.github.matteas.nontrivial.lexer.CharacterIterator;

class StringLanguageTest {
    @Test
    void countNesting() {
        final var lang = new StringLanguage<Integer>(
            (tokenContents, kind) -> 0,
            ((left, right) -> 0),
            (Class<StringLanguage<Integer>.StringTokenKind>)(Class<?>)StringLanguage.StringTokenKind.class // ewww
        );
        final var lparen = lang.token('(');
        final var rparen = lang.token(')');
        final var helloworld = lang.token("Hello World!");
        
        final var expression = lang.rule();
        expression
            .is(helloworld).or(lparen, expression, rparen)
            .map(value -> value + 1);

        final var lexer = lang.lexer(List.of(lparen, rparen, helloworld));
        final var parser = lang.parser(expression);
        
        assertEquals(
            8,
            parser.parse(
                lexer.tokenize(
                    new CharacterIterator("((((((((Hello World!))))))))")
                )
            ).expectOk().value
        );
    }
}