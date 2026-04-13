package com.toast.dpt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton backing store for all documents.
 *
 * Documents are numbered starting from 1 (docId = 1 maps to index 0 internally).
 * Each document is an ordered list of strings (tokens).
 *
 * Usage:
 *   DocumentStore store = DocumentStore.getInstance();
 *   store.addDocument(Arrays.asList("cat", "dog", "tiger")); // becomes doc 1
 *   store.addDocument(Arrays.asList("yellow", "red", "cat")); // becomes doc 2
 */
public class DocumentStore {

    private static final DocumentStore INSTANCE = new DocumentStore();

    // documents.get(i) is the token list for docId (i + 1)
    private final List<List<String>> documents = new ArrayList<>();

    private DocumentStore() {}

    public static DocumentStore getInstance() {
        return INSTANCE;
    }

    /** Append a new document; returns its assigned docId (1-based). */
    public int addDocument(List<String> tokens) {
        documents.add(Collections.unmodifiableList(new ArrayList<>(tokens)));
        return documents.size(); // 1-based
    }

    /** Returns the token list for the given 1-based docId, or null if out of range. */
    public List<String> getDocument(int docId) {
        if (docId < 1 || docId > documents.size()) {
            return null;
        }
        return documents.get(docId - 1);
    }

    /** Total number of documents currently held. */
    public int getDocCount() {
        return documents.size();
    }

    /** Removes all documents (useful between tests). */
    public void clear() {
        documents.clear();
    }
}
