package com.appspot.yanotepad.model;

import java.util.Date;

public class EntryDetails extends Entry {
    private final String contents;

    public EntryDetails(Entry entry, String contents) {
        super(entry);

        this.contents = contents;
    }

    private static String getHeader(String contents) {
        int endOfHeader = contents.indexOf('\n');
        return endOfHeader != -1 ? contents.substring(0,endOfHeader) : contents;
    }

    public EntryDetails(String header, Date timestamp, String contents) {
        super(header, timestamp);

        this.contents = contents;
    }

    public EntryDetails(String contents, Date timestamp) {
        this(getHeader(contents), timestamp, contents);
    }

    public String getContents() {
        return contents;
    }
}
