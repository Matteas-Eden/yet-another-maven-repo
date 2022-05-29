package io.github.matteas.nontrivial.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import io.github.matteas.nontrivial.util.CharacterIterator;

class StringLanguageTest {
    @Test
    void countNesting() {
        final var lang = new StringLanguage<Integer>(
            (tokenContents, kind) -> 0,
            ((left, right) -> 0),
            (Class<StringLanguage<Integer>.StringTokenKind>)(Class<?>)StringLanguage.StringTokenKind.class // ewww
        );
        final var lparen = lang.token('(').withDebugName("lparen");
        final var rparen = lang.token(')').withDebugName("rparent");
        final var helloworld = lang.token("Hello World!").withDebugName("helloworld");
        
        final var expression = lang.rule();
        expression
            .is(helloworld)
            .or(lparen, expression, rparen)
            .map(value -> value + 1);

        final var lexer = lang.lexer(List.of(lparen, rparen, helloworld));
        final var parser = lang.parser(expression);
        
        final var tokens = lexer.tokenize(
            new CharacterIterator("(((Hello World!)))")
        );
        final var tokenKinds = tokens
            .stream()
            .map(Token::kind)
            .collect(Collectors.toList());
        assertEquals(List.of(
            lparen,
            lparen,
            lparen,
            helloworld,
            rparen,
            rparen,
            rparen
        ), tokenKinds);

        final int finalValue = parser.parse(tokens).expectOk().value.value;
        assertEquals(8, finalValue);
    }
}