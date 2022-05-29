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

    public Result<V, K, T> parse(Iterable<T> tokens) {
        return parse(tokens.iterator());
    }
    
    public Result<V, K, T> parse(Iterator<T> tokens) {
        var currentFocus = focus;
        while (tokens.hasNext()) {
            final var token = tokens.next();
            System.out.println("Parse: next token " + token.toString() + " with acceptable kinds: " + Arrays.toString(currentFocus.syntax.acceptableKinds.toArray()) + " and can complete: " + currentFocus.syntax.canComplete.map(v -> "Some(" + v + ")").orElse("None"));
            final var nextFocus = next(token, currentFocus);
            if (!nextFocus.isPresent()) {
                return new Result.UnexpectedToken<>(token, new Parser<>(focus));
            }
            currentFocus = nextFocus.get();
        }
        final var finalFocus = currentFocus;
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
                    throw new RuntimeException("Unexpected token " + unexpectedToken.token.toString());
                },
                unexpectedEnd -> {
                    // TODO: Improve error reporting
                    final var expectedTokenKinds = Arrays.toString(unexpectedEnd.expected.toArray());
                    throw new RuntimeException("Unexpected end. Expected " + expectedTokenKinds);
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