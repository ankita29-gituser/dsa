package com.toast.dpt;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterator that walks every occurrence of a given string across all documents
 * held in {@link DocumentStore}, in document-then-position order.
 *
 * All occurrences are collected once at construction time into a sorted list,
 * making get/advance/isValid O(1) and seek O(log k) where k = total matches.
 */
public class Cursor {

    private final String searchStr;
    private final List<DPT> matches; // sorted by (docId ASC, position ASC)
    private int index;               // points to the current match

    /**
     * Scans all documents in {@link DocumentStore} for {@code str} and
     * positions the cursor at the first match (or marks it invalid if none).
     */
    public Cursor(String str) {
        this.searchStr = str;
        this.matches = new ArrayList<>();
        this.index = 0;

        DocumentStore store = DocumentStore.getInstance();
        for (int docId = 1; docId <= store.getDocCount(); docId++) {
            List<String> doc = store.getDocument(docId);
            for (int pos = 0; pos < doc.size(); pos++) {
                if (doc.get(pos).equals(str)) {
                    matches.add(new DPT(docId, pos));
                }
            }
        }
        // matches is already sorted because we iterate docId then position in order
    }

    /**
     * Returns the DPT at the current cursor position.
     * Behaviour is undefined if {@link #isValid()} is false.
     */
    public DPT get() {
        return matches.get(index);
    }

    /**
     * Moves the cursor to the next occurrence of the search string.
     * Does nothing if the cursor is already invalid.
     */
    public void advance() {
        if (index < matches.size()) {
            index++;
        }
    }

    /** Returns {@code true} while the cursor points to a valid match. */
    public boolean isValid() {
        return index < matches.size();
    }

    /**
     * Positions the cursor at the first match whose location is >= {@code dpt}
     * (compared first by docId, then by position).
     *
     * @return {@code true}  if {@code searchStr} exists exactly at {@code dpt};
     *         {@code false} if not — in which case the cursor is moved to the
     *                       next match after {@code dpt} (or becomes invalid
     *                       if no such match exists).
     */
    public boolean seek(DPT dpt) {
        // Binary search for the leftmost match >= dpt, starting from current index
        // so that the cursor never moves backwards.
        int lo = index;
        int hi = matches.size(); // exclusive upper bound (invalid sentinel)

        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (compare(matches.get(mid), dpt) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }

        index = lo;

        if (!isValid()) {
            return false;
        }

        DPT found = matches.get(index);
        return found.docId == dpt.docId && found.position == dpt.position;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Lexicographic comparison: docId first, then position. */
    private static int compare(DPT a, DPT b) {
        if (a.docId != b.docId) {
            return Integer.compare(a.docId, b.docId);
        }
        return Integer.compare(a.position, b.position);
    }
}
