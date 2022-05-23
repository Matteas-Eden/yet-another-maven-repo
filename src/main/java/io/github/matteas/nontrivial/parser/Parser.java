package io.github.matteas.nontrivial.parser;

import java.util.Optional;
import java.util.Iterator;
import java.util.function.Function;
import java.util.Set;

public class Parser<
    V extends Value<V>,
    K extends TokenKind,
    T extends Token<V, K>
> {
    public final Focus<V, K> focus;
    
    public Parser(ValidSyntax<V, K> syntax) {
        this(new Focus<V, K>(syntax));
    }

    public Parser(Focus<V, K> focus) {
        this.focus = focus;
    }
    
    public Result<V, K, T> parse(Iterator<T> tokens, Focus<V, K> focus) {
        while (tokens.hasNext()) {
            final var token = tokens.next();
            final var nextFocus = next(token, focus);
            if (!nextFocus.isPresent()) {
                return new Result.UnexpectedToken<>(token, new Parser<>(focus));
            }
            focus = nextFocus.get();
        }
        final var finalFocus = focus;
        return focus.syntax.canComplete
            .<Result<V, K, T>>map(
                value -> new Result.Ok<>(value, new Parser<>(finalFocus))
            )
            .orElseGet(
                () -> new Result.UnexpectedEnd<>(
                    finalFocus.syntax.acceptableKinds,
                    new Parser<>(finalFocus)
                )
            );
    }
    
    public Optional<Focus<V, K>> next(T token, Focus<V, K> focus) {
        return focus
            .refocusToNext(token.kind())
            .map(nextFocus -> nextFocus.withValue(token.value()));
    }

    public interface Result<
        V extends Value<V>,
        K extends TokenKind,
        T extends Token<V, K>
    > {
        <R> R match(
            Function<Ok<V, K, T>, R> ok,
            Function<UnexpectedToken<V, K, T>, R> unexpectedToken,
            Function<UnexpectedEnd<V, K, T>, R> unexpectedEnd
        );
        
        public static class Ok<
            V extends Value<V>,
            K extends TokenKind,
            T extends Token<V, K>
        > implements Result<V, K, T> {
            public final V value;
            public final Parser<V, K, T> parser;
            
            public Ok(V value, Parser<V, K, T> parser) {
                this.value = value;
                this.parser = parser;
            }

            @Override
            public <R> R match(
                Function<Ok<V, K, T>, R> ok,
                Function<UnexpectedToken<V, K, T>, R> unexpectedToken,
                Function<UnexpectedEnd<V, K, T>, R> unexpectedEnd
            ) {
                return ok.apply(this);
            }
        }
        
        public static class UnexpectedToken<
            V extends Value<V>,
            K extends TokenKind,
            T extends Token<V, K>
        > implements Result<V, K, T> {
            public final T token;
            public final Parser<V, K, T> parser;
            
            public UnexpectedToken(T token, Parser<V, K, T> parser) {
                this.token = token;
                this.parser = parser;
            }

            @Override
            public <R> R match(
                Function<Ok<V, K, T>, R> ok,
                Function<UnexpectedToken<V, K, T>, R> unexpectedToken,
                Function<UnexpectedEnd<V, K, T>, R> unexpectedEnd
            ) {
                return unexpectedToken.apply(this);
            }
        }
        
        public static class UnexpectedEnd<
            V extends Value<V>,
            K extends TokenKind,
            T extends Token<V, K>
        > implements Result<V, K, T> {
            public final Set<K> expected;
            public final Parser<V, K, T> parser;
            
            public UnexpectedEnd(Set<K> expected, Parser<V, K, T> parser) {
                this.expected = expected;
                this.parser = parser;
            }

            @Override
            public <R> R match(
                Function<Ok<V, K, T>, R> ok,
                Function<UnexpectedToken<V, K, T>, R> unexpectedToken,
                Function<UnexpectedEnd<V, K, T>, R> unexpectedEnd
            ) {
                return unexpectedEnd.apply(this);
            }
        }
    }
}