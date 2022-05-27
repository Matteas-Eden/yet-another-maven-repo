package io.github.matteas.nontrivial.parser;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.matteas.nontrivial.util.CyclicTree;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;
import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

public abstract class Syntax<
    V extends Value<V>, 
    K extends @NonNull Object
> extends CyclicTree<Syntax<V, K>> {
    /**
     * Also known in literature as the "FIRST" set.
     * This is the {@link Set} of {@link TokenKind} that starts
     * a token sequence which this syntax recognizes.
     */
    protected final InductiveProperty<Set<K>> acceptableKinds;
    
    /**
     * Also known in literature as the "nullability".
     */
    protected final InductiveProperty<Optional<V>> canComplete;
    
    /**
     * Also known in literature as the "productivity".
     */
    protected final InductiveProperty<Boolean> canAcceptSomeTokenSequence;
    
    protected final InductiveProperty<Set<ShouldNotFollowEntry<V, K>>> shouldNotFollow;
    protected final InductiveProperty<Set<Conflict>> conflicts;

    protected Syntax(
        InductiveProperty<Set<K>> acceptableKinds,
        InductiveProperty<Optional<V>> canComplete,
        InductiveProperty<Boolean> canAcceptSomeTokenSequence,
        InductiveProperty<Set<ShouldNotFollowEntry<V, K>>> shouldNotFollow,
        InductiveProperty<Set<Conflict>> conflicts
    ) {
        this.acceptableKinds = acceptableKinds;
        this.canComplete = canComplete;
        this.canAcceptSomeTokenSequence = canAcceptSomeTokenSequence;
        this.shouldNotFollow = shouldNotFollow;
        this.conflicts = conflicts;
    }

    public interface Conflict {}

    /**
     * Also known in literature as Nullability/Nullability conflict.
     */
    public class BothAcceptsEmptySequenceConflict implements Conflict {
        public final Disjunction<V, K> source;
        
        public BothAcceptsEmptySequenceConflict(Disjunction<V, K> source) {
            this.source = source;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            throw new UnsupportedOperationException("TODO - implement this for propagation networks to work correctly");
        }
    }
    
    /**
     * Also known in literature as First/First conflict.
     */
    public class BothAcceptsSameFirstTokenKindConflict implements Conflict {
        public final Disjunction<V, K> source;
        public final Set<K> ambiguities;
        public BothAcceptsSameFirstTokenKindConflict(Disjunction<V, K> source, Set<K> ambiguities) {
            this.source = source;
            this.ambiguities = ambiguities;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            throw new UnsupportedOperationException("TODO - implement this for propagation networks to work correctly");
        }
    }
    
    /**
     * Also known in literature as First/Follow conflict.
     */
    public class FollowConflict implements Conflict {
        public final Disjunction<V, K> source;
        public final Sequence<V, K> root;
        public final Set<K> ambiguities;
        public FollowConflict(Disjunction<V, K> source, Sequence<V, K> root, Set<K> ambiguities) {
            this.source = source;
            this.root = root;
            this.ambiguities = ambiguities;
        }
    }

    @Override
    protected Syntax<V, K> self() {
        return this;
    }

    private Optional<ValidSyntax<V, K>> validSyntax = Optional.empty();

    protected abstract ValidSyntax<V, K> createValidSyntaxUnchecked();
    protected void realizeValidSyntax() {}

    protected final ValidSyntax<V, K> getValidSyntaxUnchecked() {
        return validSyntax.get();
    }

    public final ValidationResult<V, K> validate() {
        if (conflicts.get().isEmpty()) {
            traversePostOrder(syntax -> {
                assert !syntax.validSyntax.isPresent();
                syntax.validSyntax = Optional.of(syntax.createValidSyntaxUnchecked());
            });
            
            assert validSyntax.isPresent();
            
            traversePostOrder(syntax -> {
                syntax.realizeValidSyntax();
            });
            
            return new ValidationResult.Ok<>(validSyntax.get());
        }
        return new ValidationResult.Error<>(this);
    }

    public static final class Success<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        public final V value;
        
        public Success(V value) {
            super(
                InductiveProperty.constant(Collections.emptySet()),
                InductiveProperty.constant(Optional.of(value)),
                InductiveProperty.constant(true),
                InductiveProperty.constant(Collections.emptySet()),
                InductiveProperty.constant(Collections.emptySet())
            );
            
            this.value = value;
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            return Collections.emptyIterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            return new ValidSyntax.Success<>(value);
        }
        
        @Override
        protected void realizeValidSyntax() {
            // Nothing to do.
        }
    }

    public static final class Element<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        public final K kind;
        
        public Element(K kind) {
            super(
                InductiveProperty.constant(Set.of(kind)),
                InductiveProperty.constant(Optional.empty()),
                InductiveProperty.constant(true),
                InductiveProperty.constant(Collections.emptySet()),
                InductiveProperty.constant(Collections.emptySet())
            );
            
            this.kind = kind;
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            return Collections.emptyIterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            return new ValidSyntax.Element<>(kind);
        }
    }
    
    public static final class Disjunction<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        public final Syntax<V, K> left;
        public final Syntax<V, K> right;

        public Disjunction(Syntax<V, K> left, Syntax<V, K> right) {
            this(
                left,
                right,
                InductiveProperty.deferred(Collections.emptySet()),
                InductiveProperty.deferred(Collections.emptySet())
            );
        }
        
        private Disjunction(
            Syntax<V, K> left,
            Syntax<V, K> right,
            InductiveProperty.Deferred<Set<ShouldNotFollowEntry<V, K>>> shouldNotFollow,
            InductiveProperty.Deferred<Set<Conflict>> conflicts
        ) {
            super(
                InductiveProperty.rule(
                    List.of(left.acceptableKinds, right.acceptableKinds),
                    () -> Stream.concat(
                        left.acceptableKinds.get().stream(),
                        right.acceptableKinds.get().stream()
                    ).collect(Collectors.toSet())
                ),
                InductiveProperty.rule(
                    List.of(left.canComplete, right.canComplete),
                    () -> left.canComplete.get()
                        .or(() -> right.canComplete.get())
                ),
                InductiveProperty.rule(
                    List.of(left.canAcceptSomeTokenSequence, right.canAcceptSomeTokenSequence),
                    () -> left.canAcceptSomeTokenSequence.get()
                        || right.canAcceptSomeTokenSequence.get()
                ),
                shouldNotFollow,
                conflicts
            );
            
            this.left = left;
            this.right = right;

            // We can't refer to `this` in the super constructor call, so
            // we defer the initialization of those inductive properties to
            // here:
            shouldNotFollow.realize(
                InductiveProperty.rule(
                    List.of(
                        left.acceptableKinds,
                        left.canComplete,
                        right.acceptableKinds,
                        right.canComplete
                    ),
                    () -> {
                        final Set<ShouldNotFollowEntry<V, K>> entries = Stream.concat(
                            left.shouldNotFollow.get().stream(),
                            right.shouldNotFollow.get().stream()
                        ).collect(Collectors.toCollection(HashSet::new));
                        
                        if (left.canComplete.get().isPresent()) {
                            entries.add(new ShouldNotFollowEntry<>(this, right.acceptableKinds.get()));
                        }
                        if (right.canComplete.get().isPresent()) {
                            entries.add(new ShouldNotFollowEntry<>(this, left.acceptableKinds.get()));
                        }
                        
                        return entries;
                    }
                )
            );
            conflicts.realize(
                InductiveProperty.rule(
                    List.of(
                        left.canComplete,
                        left.acceptableKinds,
                        left.shouldNotFollow,
                        left.conflicts,
                        right.canComplete,
                        right.acceptableKinds,
                        right.shouldNotFollow,
                        right.conflicts
                    ),
                    () -> {
                        final Set<Conflict> entries = Stream.concat(
                            left.conflicts.get().stream(),
                            right.conflicts.get().stream()
                        ).collect(Collectors.toCollection(HashSet::new));
                        
                        if (left.canComplete.get().isPresent()
                                && right.canComplete.get().isPresent()) {
                            entries.add(new BothAcceptsEmptySequenceConflict(this));
                        }
    
                        final Set<K> firstFirstAmbiguities = new HashSet<>(left.acceptableKinds.get());
                        firstFirstAmbiguities.retainAll(right.acceptableKinds.get());
                        if (!firstFirstAmbiguities.isEmpty()) {
                            entries.add(new BothAcceptsSameFirstTokenKindConflict(this, firstFirstAmbiguities));
                        }
                        
                        return entries;
                    }
                )
            );
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            return List.of(left, right).iterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            return new ValidSyntax.Disjunction<>(
                left.getValidSyntaxUnchecked(),
                right.getValidSyntaxUnchecked(),
                acceptableKinds.get(),
                canComplete.get(),
                canAcceptSomeTokenSequence.get(),
                shouldNotFollow.get()
            );
        }
    }
    
    public static final class Sequence<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        public final Syntax<V, K> left;
        public final Syntax<V, K> right;

        public Sequence(Syntax<V, K> left, Syntax<V, K> right) {
            this(
                left,
                right,
                InductiveProperty.deferred(Collections.emptySet())
            );
        }
        
        private Sequence(
            Syntax<V, K> left,
            Syntax<V, K> right,
            InductiveProperty.Deferred<Set<Conflict>> conflicts
        ) {
            super(
                InductiveProperty.rule(
                    List.of(
                        left.acceptableKinds,
                        left.canComplete,
                        right.canAcceptSomeTokenSequence,
                        right.acceptableKinds
                    ),
                    () -> {
                        final Set<K> kinds = new HashSet<>();
                        if (right.canAcceptSomeTokenSequence.get()) {
                            kinds.addAll(left.acceptableKinds.get());
                        }
                        if (left.canComplete.get().isPresent()) {
                            kinds.addAll(right.acceptableKinds.get());
                        }
                        return kinds;
                    }
                ),
                InductiveProperty.rule(
                    List.of(left.canComplete, right.canComplete),
                    () -> left.canComplete.get().flatMap(
                        leftValue -> right.canComplete.get().map(
                            /*
                             * TODO:
                             * error: [return] incompatible types in return.
                             * type of expression: V extends @Initialized @NonNull Value<V>
                             * method return type: V extends @Initialized @Nullable Value<V extends @Initialized @NonNull Value<V>>
                            */
                            rightValue -> castNonNull(rightValue.prepend(leftValue))
                        )
                    )
                ),
                InductiveProperty.rule(
                    List.of(left.canAcceptSomeTokenSequence, right.canAcceptSomeTokenSequence),
                    () -> left.canAcceptSomeTokenSequence.get()
                        && right.canAcceptSomeTokenSequence.get()
                ),
                InductiveProperty.rule(
                    List.of(
                        left.shouldNotFollow,
                        left.canAcceptSomeTokenSequence,
                        right.canComplete,
                        right.shouldNotFollow
                    ),
                    () -> {
                        final Set<ShouldNotFollowEntry<V, K>> entries = new HashSet<>();
                        if (right.canComplete.get().isPresent()) {
                            entries.addAll(left.shouldNotFollow.get());
                        }
                        if (left.canAcceptSomeTokenSequence.get()) {
                            entries.addAll(right.shouldNotFollow.get());
                        }
                        return entries;
                    }
                ),
                conflicts
            );
            
            this.left = left;
            this.right = right;
            
            // We can't refer to `this` in the super constructor call, so
            // we defer the initialization of those inductive properties to
            // here:
            conflicts.realize(
                InductiveProperty.rule(
                    List.of(
                        left.shouldNotFollow,
                        left.conflicts,
                        right.acceptableKinds,
                        right.conflicts
                    ),
                    () -> {
                        final Set<Conflict> entries = Stream.concat(
                            left.conflicts.get().stream(),
                            right.conflicts.get().stream()
                        ).collect(Collectors.toCollection(HashSet::new));
    
                        for (final var shouldNotFollowEntry : left.shouldNotFollow.get()) {
                            final Set<K> followAmbiguities = new HashSet<>(shouldNotFollowEntry.disallowedKinds);
                            followAmbiguities.retainAll(right.acceptableKinds.get());
                            if (!followAmbiguities.isEmpty()) {
                                entries.add(new FollowConflict(shouldNotFollowEntry.source, this, followAmbiguities));
                            }
                        }
                        
                        return entries;
                    }
                )
            );
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            return List.of(left, right).iterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            return new ValidSyntax.Sequence<>(
                left.getValidSyntaxUnchecked(),
                right.getValidSyntaxUnchecked(),
                acceptableKinds.get(),
                canComplete.get(),
                canAcceptSomeTokenSequence.get(),
                shouldNotFollow.get()
            );
        }
    }
    
    public static final class Transform<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        public final UnaryOperator<V> transformation;
        public final Syntax<V, K> syntax;

        public Transform(UnaryOperator<V> transformation, Syntax<V, K> syntax) {
            super(
                syntax.acceptableKinds,
                InductiveProperty.rule(
                    List.of(syntax.canComplete),
                    () -> syntax.canComplete.get().map(transformation)
                ),
                syntax.canAcceptSomeTokenSequence,
                syntax.shouldNotFollow,
                syntax.conflicts
            );
            this.transformation = transformation;
            this.syntax = syntax;
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            return List.of(syntax).iterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            return new ValidSyntax.Transform<>(
                transformation,
                syntax.getValidSyntaxUnchecked(),
                canComplete.get()
            );
        }
    }

    /**
     * Used to create recursive syntaxes.
     */
    public static class Deferred<V extends Value<V>, K extends @NonNull Object> extends Syntax<V, K> {
        private Optional<Syntax<V, K>> realizedSyntax = Optional.empty();
        private Optional<ValidSyntax.Deferred<V, K>> deferredValidSyntax = Optional.empty();
        
        private final InductiveProperty.Deferred<Set<K>> deferredAcceptableKinds;
        private final InductiveProperty.Deferred<Optional<V>> deferredCanComplete;
        private final InductiveProperty.Deferred<Boolean> deferredCanAcceptSomeTokenSequence;
        private final InductiveProperty.Deferred<Set<ShouldNotFollowEntry<V, K>>> deferredShouldNotFollow;
        private final InductiveProperty.Deferred<Set<Conflict>> deferredConflicts;

        public Deferred() {
            this(
                InductiveProperty.deferred(Collections.emptySet()),
                InductiveProperty.deferred(Optional.empty()),
                InductiveProperty.deferred(false),
                InductiveProperty.deferred(Collections.emptySet()),
                InductiveProperty.deferred(Collections.emptySet())
            );
        }

        private Deferred(
            InductiveProperty.Deferred<Set<K>> acceptableKinds,
            InductiveProperty.Deferred<Optional<V>> canComplete,
            InductiveProperty.Deferred<Boolean> canAcceptSomeTokenSequence,
            InductiveProperty.Deferred<Set<ShouldNotFollowEntry<V, K>>> shouldNotFollow,
            InductiveProperty.Deferred<Set<Conflict>> conflicts
        ) {
            super(
                acceptableKinds,
                canComplete,
                canAcceptSomeTokenSequence,
                shouldNotFollow,
                conflicts
            );
            this.deferredAcceptableKinds = acceptableKinds;
            this.deferredCanComplete = canComplete;
            this.deferredCanAcceptSomeTokenSequence = canAcceptSomeTokenSequence;
            this.deferredShouldNotFollow = shouldNotFollow;
            this.deferredConflicts = conflicts;
        }

        protected void realize(Syntax<V, K> syntax) {
            realizedSyntax = Optional.of(syntax);
            deferredAcceptableKinds.realize(syntax.acceptableKinds);
            deferredCanComplete.realize(syntax.canComplete);
            deferredCanAcceptSomeTokenSequence.realize(syntax.canAcceptSomeTokenSequence);
            deferredShouldNotFollow.realize(syntax.shouldNotFollow);
            deferredConflicts.realize(syntax.conflicts);
        }

        public Optional<Syntax<V, K>> realized() {
            return realizedSyntax;
        }

        @Override
        public Iterator<Syntax<V, K>> children() {
            assert realizedSyntax.isPresent();
            return List.of(realizedSyntax.get()).iterator();
        }
        
        @Override
        protected ValidSyntax<V, K> createValidSyntaxUnchecked() {
            assert realizedSyntax.isPresent();
            deferredValidSyntax = Optional.of(
                new ValidSyntax.Deferred<>(
                    acceptableKinds.get(),
                    canComplete.get(),
                    canAcceptSomeTokenSequence.get(),
                    shouldNotFollow.get()
                )
            );
            return deferredValidSyntax.get();
        }
        
        @Override
        protected void realizeValidSyntax() {
            assert deferredValidSyntax.isPresent();
            assert realizedSyntax.isPresent();
            deferredValidSyntax.get().realize(
                realizedSyntax.get().getValidSyntaxUnchecked()
            );
        }
    }
}