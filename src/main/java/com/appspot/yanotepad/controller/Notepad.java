package com.appspot.yanotepad.controller;

import com.appspot.yanotepad.model.Entry;
import com.appspot.yanotepad.model.EntryDetails;
import com.google.appengine.api.search.*;
import com.google.appengine.api.users.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Notepad extends BaseController {
    static final String HEADER_FIELD = "header";
    static final String CONTENT_FIELD = "content";
    static final String TIMESTAMP_FIELD = "timestamp";

    public Notepad(User user) {
        super(user);
    }

    public EntryDetails addEntry(String content)
    {
        EntryDetails result = new EntryDetails(content, now());

        addEntry(result);

        return result;
    }

    public String addEntry(EntryDetails entry)
    {
        Document document = Document.newBuilder()
                .addField(Field.newBuilder().setName(HEADER_FIELD).setText(entry.getHeader()))
                .addField(Field.newBuilder().setName(CONTENT_FIELD).setText(entry.getContent()))
                .addField(Field.newBuilder().setName(TIMESTAMP_FIELD).setDate(entry.getTimestamp()))
                .build();

        PutResponse response = getIndex().put(document);

        OperationResult operationResult = response.getResults().get(0);
        if (operationResult.getCode() != StatusCode.OK)
        {
            throw new PutException(operationResult);
        }

        String documentId = response.getIds().get(0);
        entry.setDocumentId(documentId);

        return documentId;
    }

    public EntryDetails updateEntry(String documentId, String content)
    {
        EntryDetails result = new EntryDetails(content, now());
        result.setDocumentId(documentId);

        updateEntry(result);

        return result;
    }

    public void updateEntry(EntryDetails entry)
    {
        Document document = Document.newBuilder()
                .addField(Field.newBuilder().setName(HEADER_FIELD).setText(entry.getHeader()))
                .addField(Field.newBuilder().setName(CONTENT_FIELD).setText(entry.getContent()))
                .addField(Field.newBuilder().setName(TIMESTAMP_FIELD).setDate(entry.getTimestamp()))
                .setId(entry.getDocumentId())
                .build();

        PutResponse response = getIndex().put(document);

        OperationResult operationResult = response.getResults().get(0);
        if (operationResult.getCode() != StatusCode.OK)
        {
            throw new PutException(operationResult);
        }
    }

    public List<Entry> searchEntries(String query) {
        if (query == null)
        {
            query = "";
        }

        return toEntries(getIndex().search(Query.newBuilder().setOptions(getQueryOptions()).build(query)).getResults());
    }

    public EntryDetails getEntryDetails(String documentId)
    {
        Document document = getIndex().get(documentId);
        if ( document == null )
        {
            return null;
        }

        String header = document.getOnlyField(HEADER_FIELD).getText();
        String content = document.getOnlyField(CONTENT_FIELD).getText();
        Date timestamp = document.getOnlyField(TIMESTAMP_FIELD).getDate();

        EntryDetails result = new EntryDetails(header, timestamp, content);
        result.setDocumentId(documentId);

        return result;
    }

    public void deleteEntry(String documentId) {
        getIndex().delete(documentId);
    }

    private List<Entry> toEntries(Collection<ScoredDocument> documents) {
        ArrayList<Entry> result = new ArrayList<>(documents.size());

        for (ScoredDocument document : documents) {
            result.add(toEntry(document));
        }

        return result;
    }

    private Entry toEntry(ScoredDocument document) {
        String header = document.getOnlyField(HEADER_FIELD).getText();
        Date timestamp = document.getOnlyField(TIMESTAMP_FIELD).getDate();

        Entry result = new Entry(header, timestamp);
        result.setDocumentId(document.getId());

        return result;
    }
}
