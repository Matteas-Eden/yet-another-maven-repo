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
        final var lparen = lang.token('(');
        System.out.println("lparen = " + lparen);
        final var rparen = lang.token(')');
        System.out.println("rparen = " + rparen);
        final var helloworld = lang.token("Hello World!");
        System.out.println("helloworld = " + helloworld);
        
        final var expression = lang.rule();
        System.out.println("expression = " + expression);
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
        
        assertEquals(8, parser.parse(tokens).expectOk().value);
    }
}