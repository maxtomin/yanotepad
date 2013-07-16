package com.appspot.yanotepad.model;

import java.util.Date;

public class Entry {
    private final String header;
    private final Date timestamp;

    private String documentId;

    public Entry(String header, Date timestamp) {
        this.header = header;
        this.timestamp = timestamp;
    }

    public Entry(Entry entry) {
        this(entry.getHeader(), entry.getTimestamp());

        this.documentId = entry.documentId;
    }

    public String getHeader() {
        return header;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isPersisted()
    {
        return documentId != null;
    }
}
