package com.toast.dpt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Documents under test (docIds are 1-based):
 *   doc1: cat, dog, tiger
 *   doc2: yellow, red, cat
 *   doc3: dog, up
 */
class CursorTest {

    @BeforeEach
    void setup() {
        DocumentStore store = DocumentStore.getInstance();
        store.clear();
        store.addDocument(Arrays.asList("cat", "dog", "tiger"));   // docId 1
        store.addDocument(Arrays.asList("yellow", "red", "cat"));  // docId 2
        store.addDocument(Arrays.asList("dog", "up"));             // docId 3
    }

    // ------------------------------------------------------------------
    // isValid / get / advance
    // ------------------------------------------------------------------

    @Test
    void noMatchesIsInvalid() {
        Cursor c = new Cursor("elephant");
        assertFalse(c.isValid());
    }

    @Test
    void singleMatch() {
        Cursor c = new Cursor("tiger");
        assertTrue(c.isValid());
        assertEquals(1, c.get().docId);
        assertEquals(2, c.get().position);

        c.advance();
        assertFalse(c.isValid());
    }

    @Test
    void multipleMatchesAcrossDocs() {
        // "cat" appears at doc1:pos0 and doc2:pos2
        Cursor c = new Cursor("cat");

        assertTrue(c.isValid());
        assertEquals(1, c.get().docId);
        assertEquals(0, c.get().position);

        c.advance();
        assertTrue(c.isValid());
        assertEquals(2, c.get().docId);
        assertEquals(2, c.get().position);

        c.advance();
        assertFalse(c.isValid());
    }

    @Test
    void dogAppearsInTwoDocs() {
        // "dog" at doc1:pos1 and doc3:pos0
        Cursor c = new Cursor("dog");

        assertTrue(c.isValid());
        assertEquals(1, c.get().docId);
        assertEquals(1, c.get().position);

        c.advance();
        assertEquals(3, c.get().docId);
        assertEquals(0, c.get().position);

        c.advance();
        assertFalse(c.isValid());
    }

    // ------------------------------------------------------------------
    // seek — exact hit
    // ------------------------------------------------------------------

    @Test
    void seekExactHitReturnsTrue() {
        Cursor c = new Cursor("cat");
        assertTrue(c.seek(new DPT(2, 2))); // cat at doc2:pos2
        assertEquals(2, c.get().docId);
        assertEquals(2, c.get().position);
    }

    @Test
    void seekFirstPositionReturnsTrue() {
        Cursor c = new Cursor("dog");
        assertTrue(c.seek(new DPT(1, 1)));
        assertEquals(1, c.get().docId);
    }

    // ------------------------------------------------------------------
    // seek — no exact hit: cursor advances to next match
    // ------------------------------------------------------------------

    @Test
    void seekMissAdvancesToNextMatch() {
        // "cat" matches: doc1:0, doc2:2
        // seek doc1:pos1 → no match there; next match is doc2:pos2
        Cursor c = new Cursor("cat");
        assertFalse(c.seek(new DPT(1, 1)));
        assertTrue(c.isValid());
        assertEquals(2, c.get().docId);
        assertEquals(2, c.get().position);
    }

    @Test
    void seekPastAllMatchesBecomesInvalid() {
        Cursor c = new Cursor("tiger"); // only doc1:pos2
        assertFalse(c.seek(new DPT(3, 0))); // past last match
        assertFalse(c.isValid());
    }

    @Test
    void seekNonExistentDocBecomesInvalid() {
        Cursor c = new Cursor("up"); // only doc3:pos1
        assertFalse(c.seek(new DPT(99, 0)));
        assertFalse(c.isValid());
    }

    // ------------------------------------------------------------------
    // getDocIds helper (the earlier exercise)
    // ------------------------------------------------------------------

    @Test
    void getDocIdsForCat() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2));
        assertEquals(expected, getDocIds("cat"));
    }

    @Test
    void getDocIdsForDog() {
        Set<Integer> expected = new HashSet<>(Arrays.asList(1, 3));
        assertEquals(expected, getDocIds("dog"));
    }

    @Test
    void getDocIdsForMissingWord() {
        assertTrue(getDocIds("elephant").isEmpty());
    }

    private Set<Integer> getDocIds(String str) {
        Set<Integer> result = new HashSet<>();
        Cursor c = new Cursor(str);
        while (c.isValid()) {
            result.add(c.get().docId);
            c.advance();
        }
        return result;
    }
}
