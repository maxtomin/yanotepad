package com.appspot.yanotepad.model;

import java.util.Date;

public class EntryDetails extends Entry {
    private final String content;

    private static String getHeader(String content) {
        int endOfHeader = content.indexOf('\n');
        return endOfHeader != -1 ? content.substring(0,endOfHeader) : content;
    }

    public EntryDetails(String header, Date timestamp, String content) {
        super(header, timestamp);

        this.content = content;
    }

    public EntryDetails(String content, Date timestamp) {
        this(getHeader(content), timestamp, content);
    }

    public String getContent() {
        return content;
    }
}
