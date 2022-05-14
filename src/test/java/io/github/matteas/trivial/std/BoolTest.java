package io.github.matteas.trivial.std;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.matteas.trivial.Repl;
import static io.github.matteas.trivial.combinator.ffi.BoolExporter.exportBool;

class BoolTest {
    @Test
    void constants() throws Exception {
        final var repl = new Repl();
        assertEquals(true, exportBool(repl.eval("Bool.True").get()));
        assertEquals(false, exportBool(repl.eval("Bool.False").get()));
    }
    
    @Test
    void not() throws Exception {
        final var repl = new Repl();
        assertEquals(true, exportBool(repl.eval("Bool.Not Bool.False").get()));
        assertEquals(false, exportBool(repl.eval("Bool.Not Bool.True").get()));
    }
    
    @Test
    void or() throws Exception {
        final var repl = new Repl();
        assertEquals(false, exportBool(repl.eval("Bool.Or Bool.False Bool.False").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Or Bool.True Bool.False").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Or Bool.False Bool.True").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Or Bool.True Bool.True").get()));
    }
    
    @Test
    void and() throws Exception {
        final var repl = new Repl();
        assertEquals(false, exportBool(repl.eval("Bool.And Bool.False Bool.False").get()));
        assertEquals(false, exportBool(repl.eval("Bool.And Bool.True Bool.False").get()));
        assertEquals(false, exportBool(repl.eval("Bool.And Bool.False Bool.True").get()));
        assertEquals(true, exportBool(repl.eval("Bool.And Bool.True Bool.True").get()));
    }
    
    @Test
    void xor() throws Exception {
        final var repl = new Repl();
        assertEquals(false, exportBool(repl.eval("Bool.Xor Bool.False Bool.False").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Xor Bool.True Bool.False").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Xor Bool.False Bool.True").get()));
        assertEquals(false, exportBool(repl.eval("Bool.Xor Bool.True Bool.True").get()));
    }
    
    @Test
    void eq() throws Exception {
        final var repl = new Repl();
        assertEquals(true, exportBool(repl.eval("Bool.Eq Bool.False Bool.False").get()));
        assertEquals(false, exportBool(repl.eval("Bool.Eq Bool.True Bool.False").get()));
        assertEquals(false, exportBool(repl.eval("Bool.Eq Bool.False Bool.True").get()));
        assertEquals(true, exportBool(repl.eval("Bool.Eq Bool.True Bool.True").get()));
    }
}