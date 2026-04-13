package com.toast.dpt;

/**
 * Represents a match location: the document ID and the position (0-based index)
 * of a string within that document.
 */
public class DPT {
    public int docId;    // 1-based document identifier
    public int position; // 0-based index within the document

    public DPT() {}

    public DPT(int docId, int position) {
        this.docId = docId;
        this.position = position;
    }

    @Override
    public String toString() {
        return "DPT{docId=" + docId + ", position=" + position + "}";
    }
}
