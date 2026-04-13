package com.toast.dpt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Standalone smoke-test (no JUnit) for DPT / DocumentStore / Cursor.
 *
 * Documents:
 *   doc1: cat, dog, tiger
 *   doc2: yellow, red, cat
 *   doc3: dog, up
 */
public class CursorRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        setup();

        // --- isValid / get / advance ---
        test("noMatch_isInvalid", () -> {
            Cursor c = new Cursor("elephant");
            assertTrue(!c.isValid(), "cursor should be invalid");
        });

        test("singleMatch_tiger", () -> {
            Cursor c = new Cursor("tiger");
            assertTrue(c.isValid(), "should be valid");
            assertEquals(1, c.get().docId, "docId");
            assertEquals(2, c.get().position, "position");
            c.advance();
            assertTrue(!c.isValid(), "should be invalid after advance");
        });

        test("multipleMatches_cat", () -> {
            Cursor c = new Cursor("cat");
            assertTrue(c.isValid(), "1st match valid");
            assertEquals(1, c.get().docId, "1st docId");
            assertEquals(0, c.get().position, "1st pos");
            c.advance();
            assertTrue(c.isValid(), "2nd match valid");
            assertEquals(2, c.get().docId, "2nd docId");
            assertEquals(2, c.get().position, "2nd pos");
            c.advance();
            assertTrue(!c.isValid(), "done");
        });

        test("multipleMatches_dog", () -> {
            Cursor c = new Cursor("dog");
            assertEquals(1, c.get().docId, "dog doc1");
            assertEquals(1, c.get().position, "dog pos1");
            c.advance();
            assertEquals(3, c.get().docId, "dog doc3");
            assertEquals(0, c.get().position, "dog pos3");
            c.advance();
            assertTrue(!c.isValid(), "done");
        });

        // --- seek: exact hit ---
        test("seek_exactHit_returnsTrue", () -> {
            Cursor c = new Cursor("cat");
            assertTrue(c.seek(new DPT(2, 2)), "seek(2,2) should return true");
            assertEquals(2, c.get().docId, "docId");
            assertEquals(2, c.get().position, "pos");
        });

        test("seek_firstPosition_returnsTrue", () -> {
            Cursor c = new Cursor("dog");
            assertTrue(c.seek(new DPT(1, 1)), "seek(1,1) should return true");
            assertEquals(1, c.get().docId, "docId");
        });

        // --- seek: miss → next match ---
        test("seek_miss_advancesToNext", () -> {
            // cat matches: (1,0), (2,2) — seek (1,1) misses → lands on (2,2)
            Cursor c = new Cursor("cat");
            assertTrue(!c.seek(new DPT(1, 1)), "seek(1,1) should return false");
            assertTrue(c.isValid(), "cursor still valid");
            assertEquals(2, c.get().docId, "next docId");
            assertEquals(2, c.get().position, "next pos");
        });

        test("seek_pastAll_becomesInvalid", () -> {
            Cursor c = new Cursor("tiger"); // only (1,2)
            assertTrue(!c.seek(new DPT(3, 0)), "seek past last");
            assertTrue(!c.isValid(), "should be invalid");
        });

        test("seek_nonExistentDoc_becomesInvalid", () -> {
            Cursor c = new Cursor("up"); // only (3,1)
            assertTrue(!c.seek(new DPT(99, 0)), "seek doc 99");
            assertTrue(!c.isValid(), "should be invalid");
        });

        // --- getDocIds helper ---
        test("getDocIds_cat", () -> {
            Set<Integer> result = getDocIds("cat");
            assertEquals(new HashSet<>(Arrays.asList(1, 2)), result, "doc ids for cat");
        });

        test("getDocIds_dog", () -> {
            Set<Integer> result = getDocIds("dog");
            assertEquals(new HashSet<>(Arrays.asList(1, 3)), result, "doc ids for dog");
        });

        test("getDocIds_missing", () -> {
            assertTrue(getDocIds("elephant").isEmpty(), "no docs for elephant");
        });

        // --- summary ---
        System.out.println("\n=== Results: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    static void setup() {
        DocumentStore store = DocumentStore.getInstance();
        store.clear();
        store.addDocument(Arrays.asList("cat", "dog", "tiger"));   // docId 1
        store.addDocument(Arrays.asList("yellow", "red", "cat"));  // docId 2
        store.addDocument(Arrays.asList("dog", "up"));             // docId 3
    }

    static Set<Integer> getDocIds(String str) {
        Set<Integer> result = new HashSet<>();
        Cursor c = new Cursor(str);
        while (c.isValid()) {
            result.add(c.get().docId);
            c.advance();
        }
        return result;
    }

    @FunctionalInterface interface TestBody { void run() throws Exception; }

    static void test(String name, TestBody body) {
        try {
            setup(); // reset store before each test
            body.run();
            System.out.println("  PASS  " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL  " + name + " — " + e.getMessage());
            failed++;
        }
    }

    static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void assertEquals(Object expected, Object actual, String msg) {
        if (!expected.equals(actual))
            throw new AssertionError(msg + ": expected " + expected + " but got " + actual);
    }
}
