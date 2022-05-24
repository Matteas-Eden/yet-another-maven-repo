package io.github.matteas.nontrivial.parser;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class StringLanguage<V, K> extends Language<StringLanguage.SimpleValue<V>, K, StringLanguage.StringTokenKind> {
    public final BiFunction<String, StringTokenKind, V> tokenToValue;
    public final BinaryOperator<V> joiner;

    public StringLanguage(
        Map<K, String> dictionary,
        BiFunction<String, StringTokenKind, V> tokenToValue,
        BinaryOperator<V> joiner,
        Class<K> kindKeyClass
    ) {
    }
    
    public StringLanguage(
        Map<K, String> dictionary,
        BiFunction<String, StringTokenKind, V> tokenToValue,
        BinaryOperator<V> joiner,
        Class<K> kindKeyClass
    ) {
        super(dictionary
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    return new StringTokenKind(entry.getValue());
                }
            )),
            kindKeyClass
        );
        this.tokenToValue = tokenToValue;
        this.joiner = joiner;
    }
    
    public static class StringToken<V extends Value<V>> implements Token<V, StringTokenKind> {
        public final StringTokenKind kind;
        public final String contents;
        public final BiFunction<String, StringTokenKind, V> tokenToValue;

        public StringToken(StringTokenKind kind, String contents, BiFunction<String, StringTokenKind, V> tokenToValue) {
            this.kind = kind;
            this.contents = contents;
            this.tokenToValue = tokenToValue;
        }

        @Override
        public StringTokenKind kind() {
            return kind;
        }
        
        @Override
        public V value() {
            return tokenToValue.apply(contents, kind);
        }
    }

    public static class StringTokenKind implements TokenKind {}

    public static class SimpleValue<V> implements Value<SimpleValue<V>> {
        public final V value;
        public final BinaryOperator<V> joiner;
        
        public SimpleValue(V value, BinaryOperator<V> joiner) {
            this.value = value;
            this.joiner = joiner;
        }

        @Override
        public SimpleValue<V> prepend(SimpleValue<V> left) {
            return new SimpleValue<>(joiner.apply(left.value, value), joiner);
        }
    }
}