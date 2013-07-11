package com.appspot.yanotepad.controller;

import com.google.appengine.api.search.Index;

import java.util.Date;

import static org.mockito.Mockito.mock;

public class NotepadMock extends Notepad {
    private final Index index = mock(Index.class);
    private Date now = new Date();

    public NotepadMock() {
        super(null);
    }

    @Override
    public Index getIndex() {
        return index;
    }

    @Override
    protected Date now() {
        return now;
    }
}
