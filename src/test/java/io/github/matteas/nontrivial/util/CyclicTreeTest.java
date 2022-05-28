package io.github.matteas.nontrivial.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;

class CyclicTreeTest {
    class SingletonGraph extends CyclicTree<SingletonGraph> {
        public int counter = 0;
        public int otherCounter = 0;
        
        @Override
        public SingletonGraph self() {
            return this;
        }

        @Override
        public Iterator<SingletonGraph> children() {
            return Collections.emptyIterator();
        }
    }
    
    class MutableNode extends CyclicTree<MutableNode> {
        public List<MutableNode> children = List.of();
        
        @Override
        public MutableNode self() {
            return this;
        }

        @Override
        public Iterator<MutableNode> children() {
            return children.iterator();
        }
    }
    
    @Test
    void singleNodeIsVisitedOnce() {
        final var node = new SingletonGraph();
        node.traversePostOrder(n -> {
            assertEquals(node, n);
            n.counter++;
        });
        assertEquals(1, node.counter);
    }
    
    @Test
    void singleNodeCanBeVisitedByAnotherVisitor() {
        final var node = new SingletonGraph();
        node.traversePostOrder(n -> {
            assertEquals(node, n);
            n.counter++;
        });
        node.traversePostOrder(n -> {
            assertEquals(node, n);
            n.otherCounter++;
        });
        assertEquals(1, node.counter);
        assertEquals(1, node.otherCounter);
    }

    @Test
    void linearGraphIsVisitedBottomUp() {
        final var a = new MutableNode();
        final var b = new MutableNode();
        final var c = new MutableNode();
        a.children = List.of(b);
        b.children = List.of(c);
        final List<MutableNode> visitOrder = new ArrayList<>();
        a.traversePostOrder(node -> {
            visitOrder.add(node);
        });
        assertEquals(visitOrder, List.of(c, b, a));
    }

    @Test
    void fanOutGraphIsVisitedInPostOrder() {
        final var a = new MutableNode();
        final var b = new MutableNode();
        final var c = new MutableNode();
        final var d = new MutableNode();
        final var e = new MutableNode();
        a.children = List.of(b, c);
        c.children = List.of(d, e);
        final List<MutableNode> visitOrder = new ArrayList<>();
        a.traversePostOrder(node -> {
            visitOrder.add(node);
        });
        assertEquals(visitOrder, List.of(b, d, e, c, a));
    }

    @Test
    void diamondGraphVisitedInPostOrder() {
        final var a = new MutableNode();
        final var b = new MutableNode();
        final var c = new MutableNode();
        final var d = new MutableNode();
        a.children = List.of(b, c);
        b.children = List.of(d);
        c.children = List.of(d);
        final List<MutableNode> visitOrder = new ArrayList<>();
        a.traversePostOrder(node -> {
            visitOrder.add(node);
        });
        assertEquals(visitOrder, List.of(d, b, c, a));
    }

    @Test
    void singleCycleIsVisitedOnce() {
        final var a = new MutableNode();
        a.children = List.of(a);
        final List<MutableNode> visitOrder = new ArrayList<>();
        a.traversePostOrder(node -> {
            visitOrder.add(node);
        });
        assertEquals(visitOrder, List.of(a));
    }

    @Test
    void cyclicGraphIsVisitedInPostOrder() {
        final var a = new MutableNode();
        final var b = new MutableNode();
        final var c = new MutableNode();
        final var d = new MutableNode();
        final var e = new MutableNode();
        a.children = List.of(b, c);
        c.children = List.of(d, e);
        e.children = List.of(a);
        final List<MutableNode> visitOrder = new ArrayList<>();
        a.traversePostOrder(node -> {
            visitOrder.add(node);
        });
        assertEquals(visitOrder, List.of(b, d, e, c, a));
    }
}