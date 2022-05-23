package io.github.matteas.nontrivial.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;

class StringLanguageTest {
    @Test
    void countNesting() {
        final var lang = new StringLanguage<Integer>(
            Map.of(
                "(", "(",
                ")", ")",
                "helloworld", "Hello World!"
            ),
            (tokenContents, kind) -> 0,
            (left, right) -> 0
        );
        final var expression = lang.rule();
        expression
            .is("helloworld").or("(", expression, ")")
            .map(value -> value + 1);
        
        final var parser = new Parser<>(expression.validate().expectOk());
        assertEquals(
            8,
            parser.parse("((((((((Hello World!))))))))").expectOk().value
        );
    }
}