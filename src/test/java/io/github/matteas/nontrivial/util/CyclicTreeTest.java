package io.github.matteas.nontrivial.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

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
}