package com.appspot.yanotepad.controller;

import com.appspot.yanotepad.model.Entry;
import com.appspot.yanotepad.model.EntryDetails;
import com.google.appengine.api.search.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class NotepadTest {
    private final NotepadMock notepad = new NotepadMock();
    private final Index index = notepad.getIndex();

    private ArgumentCaptor<Document> documents = ArgumentCaptor.forClass(Document.class);

    @Test
    public void testAddEntry() throws Exception {
        mockIndexPut("1234");

        EntryDetails entry = notepad.addEntry("aaa");

        assertEquals(entry.getHeader(), "aaa");
        assertEquals(entry.getContent(), "aaa");
        assertEquals(entry.getTimestamp(), notepad.now());
        assertEquals(entry.getDocumentId(), "1234");

        Document document = documents.getValue();
        assertEquals(document.getOnlyField(Notepad.HEADER_FIELD).getText(), "aaa");
        assertEquals(document.getOnlyField(Notepad.CONTENT_FIELD).getText(), "aaa");
        assertEquals(document.getOnlyField(Notepad.TIMESTAMP_FIELD).getDate(), notepad.now());
    }

    @Test
    public void testGetEntryDetails() throws Exception {
        Document document = Document.newBuilder()
                .addField(Field.newBuilder().setName(Notepad.HEADER_FIELD).setText("header"))
                .addField(Field.newBuilder().setName(Notepad.CONTENT_FIELD).setText("header\ncontent"))
                .addField(Field.newBuilder().setName(Notepad.TIMESTAMP_FIELD).setDate(notepad.now()))
                .build();
        when(index.get("123")).thenReturn(document);

        EntryDetails entryDetails = notepad.getEntryDetails("123");

        assertEquals(entryDetails.getHeader(), "header");
        assertEquals(entryDetails.getContent(), "header\ncontent");
        assertEquals(entryDetails.getTimestamp(), notepad.now());
        assertEquals(entryDetails.getDocumentId(), "123");
    }

    @Test
    public void testSearch() throws Exception {

        ScoredDocument document = (ScoredDocument) ScoredDocument.newBuilder()
            .setId("333")
            .addField(Field.newBuilder().setName(Notepad.HEADER_FIELD).setText("header"))
            .addField(Field.newBuilder().setName(Notepad.TIMESTAMP_FIELD).setDate(notepad.now()))
            .build();

        @SuppressWarnings("unchecked")
        Results<ScoredDocument> results = mock(Results.class);
        when(results.getResults()).thenReturn(Collections.singleton(document));
        when(index.search(Mockito.<Query>any())).thenReturn(results);

        List<Entry> query = notepad.searchEntries("query");
        assertEquals(query.size(), 1);
        Entry entry = query.get(0);

        assertEquals(entry.getHeader(), "header");
        assertEquals(entry.getTimestamp(), notepad.now());
        assertEquals(entry.getDocumentId(), "333");
    }

    private void mockIndexPut(String id) {
        mockIndexPut(StatusCode.OK, null, id);
    }

    private void mockIndexPut(StatusCode code, String error, String id) {
        PutResponse response = mock(PutResponse.class);
        when(response.getResults()).thenReturn(Collections.singletonList(new OperationResult(code, error)));
        when(response.getIds()).thenReturn(Collections.singletonList(id));

        when(index.put(documents.capture())).thenReturn(response);
    }
}
