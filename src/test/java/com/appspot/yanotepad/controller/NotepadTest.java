package com.appspot.yanotepad.controller;

import com.appspot.yanotepad.model.Entry;
import com.appspot.yanotepad.model.EntryDetails;
import com.google.appengine.api.search.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NotepadTest {
    private NotepadMock notepad;
    private Index index;

    private ArgumentCaptor<Document> documents;

    @Before
    public void setUp() throws Exception {
        notepad = new NotepadMock();
        index = notepad.getIndex();

        documents = ArgumentCaptor.forClass(Document.class);
    }

    @Test
    public void testAddEntry() throws Exception {
        mockIndexPut("1234");

        EntryDetails entry = notepad.addEntry("aaa");

        assertEquals("aaa", entry.getHeader());
        assertEquals("aaa", entry.getContent());
        assertEquals(notepad.now(), entry.getTimestamp());
        assertEquals("1234", entry.getDocumentId());

        Document document = documents.getValue();
        assertEquals("aaa", document.getOnlyField(Notepad.HEADER_FIELD).getText());
        assertEquals("aaa", document.getOnlyField(Notepad.CONTENT_FIELD).getText());
        assertEquals(notepad.now(), document.getOnlyField(Notepad.TIMESTAMP_FIELD).getDate());
    }

    @Test
    public void testUpdateEntry() throws Exception {
        mockIndexPut("1234");

        Date time1 = notepad.now();
        EntryDetails entry = notepad.addEntry("aaa");

        notepad.advanceTime(1);
        Date time2 = notepad.now();
        entry = notepad.updateEntry(entry.getDocumentId(),"bbb");

        assertEquals("bbb", entry.getHeader());
        assertEquals("bbb", entry.getContent());
        assertEquals(time2, entry.getTimestamp());
        assertEquals("1234", entry.getDocumentId());

        Document document1 = documents.getAllValues().get(0);
        assertEquals("aaa", document1.getOnlyField(Notepad.HEADER_FIELD).getText());
        assertEquals("aaa", document1.getOnlyField(Notepad.CONTENT_FIELD).getText());
        assertEquals(time1, document1.getOnlyField(Notepad.TIMESTAMP_FIELD).getDate());

        Document document2 = documents.getAllValues().get(1);
        assertEquals("bbb", document2.getOnlyField(Notepad.HEADER_FIELD).getText());
        assertEquals("bbb", document2.getOnlyField(Notepad.CONTENT_FIELD).getText());
        assertEquals(time2, document2.getOnlyField(Notepad.TIMESTAMP_FIELD).getDate());
    }

    @Test
    public void testDeleteEntry() throws Exception {
        mockIndexPut("1234");

        EntryDetails entry = notepad.addEntry("aaa");
        assertEquals("1234", entry.getDocumentId());

        notepad.deleteEntry(entry.getDocumentId());
        assertNull(notepad.getEntryDetails(entry.getDocumentId()));
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

        assertEquals("header", entryDetails.getHeader());
        assertEquals("header\ncontent", entryDetails.getContent());
        assertEquals(notepad.now(), entryDetails.getTimestamp());
        assertEquals("123", entryDetails.getDocumentId());
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
        assertEquals(1, query.size());
        Entry entry = query.get(0);

        assertEquals("header", entry.getHeader());
        assertEquals(notepad.now(), entry.getTimestamp());
        assertEquals("333", entry.getDocumentId());
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
