package io.github.matteas.nontrivial.parser;

import java.util.Optional;
import java.util.Iterator;
import java.util.function.Function;
import java.util.Set;
import java.util.Arrays;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Parser<
    V extends Value<V>,
    K extends @NonNull Object,
    T extends Token<V, K>
> {
    public final Focus<V, K> focus;
    
    public Parser(ValidSyntax<V, K> syntax) {
        this(new Focus<V, K>(syntax));
    }

    public Parser(Focus<V, K> focus) {
        this.focus = focus;
    }

    public Set<K> acceptableKinds() {
        return focus.acceptableKinds();
    }

    public Optional<V> canComplete() {
        return focus.canComplete();
    }

    public Result<V, K, T> parse(Iterable<T> tokens) {
        return parse(tokens.iterator());
    }
    
    public Result<V, K, T> parse(Iterator<T> tokens) {
        var current = this;
        while (tokens.hasNext()) {
            final var token = tokens.next();
            System.out.println("Parse: next token " + token.toString() + " with parser " + current.toString());
            final var nextState = current.next(token);
            if (!nextState.isPresent()) {
                return new Result.UnexpectedToken<>(token, current);
            }
            current = nextState.get();
        }
        final var finalState = current;
        return finalState.canComplete()
            .<Result<V, K, T>>map(
                value -> new Result.Ok<>(value, finalState)
            )
            .orElseGet(
                () -> new Result.UnexpectedEnd<>(finalState)
            );
    }
    
    public Optional<Parser<V, K, T>> next(T token) {
        return focus
            .refocusToNext(token.kind())
            .map(nextFocus -> nextFocus.withValue(token.value()))
            .map(Parser::new);
    }

    @Override
    public String toString() {
        return String.format(
            "Parser(\n acceptableKinds: %s,\n canComplete: %s,\n focus:\n%s\n)",
            acceptableKindsToString(),
            canCompleteToString(),
            focus.toString().replaceAll("(?m)^", "  ")
        );
    }

    private String acceptableKindsToString() {
        return Arrays.toString(acceptableKinds().toArray());
    }

    private String canCompleteToString() {
        return canComplete()
            .map(value -> String.format("Some(%s)", value))
            .orElseGet(() -> "None");
    }

    public static abstract class Result<
        V extends Value<V>,
        K extends @NonNull Object,
        T extends Token<V, K>
    > {
        public abstract <R> R match(
            Function<Ok<V, K, T>, R> ok,
            Function<UnexpectedToken<V, K, T>, R> unexpectedToken,
            Function<UnexpectedEnd<V, K, T>, R> unexpectedEnd
        );

        public final Ok<V, K, T> expectOk() {
            return match(
                ok -> ok,
                unexpectedToken -> {
                    // TODO: Improve error reporting
                    throw new RuntimeException("Unexpected token: " + unexpectedToken.token.toString() + ", expected: " + unexpectedToken.parser.acceptableKindsToString());
                },
                unexpectedEnd -> {
                    // TODO: Improve error reporting
                    throw new RuntimeException("Unexpected end. Expected " + unexpectedEnd.parser.acceptableKindsToString());
                }
            );
        }
        
        public static class Ok<
            V extends Value<V>,
            K extends @NonNull Object,
            T extends Token<V, K>
        > extends Result<V, K, T> {
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
            K extends @NonNull Object,
            T extends Token<V, K>
        > extends Result<V, K, T> {
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
            K extends @NonNull Object,
            T extends Token<V, K>
        > extends Result<V, K, T> {
            public final Parser<V, K, T> parser;
            
            public UnexpectedEnd(Parser<V, K, T> parser) {
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