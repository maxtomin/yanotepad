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
    static final String CONTENTS_FIELD = "contents";
    static final String TIMESTAMP_FIELD = "timestamp";

    public Notepad(User user) {
        super(user);
    }

    public EntryDetails addEntry(String contents)
    {
        EntryDetails result = new EntryDetails(contents, now());

        addEntry(result);

        return result;
    }

    public String addEntry(EntryDetails entry)
    {
        Document document = Document.newBuilder()
                .addField(Field.newBuilder().setName(HEADER_FIELD).setText(entry.getHeader()))
                .addField(Field.newBuilder().setName(CONTENTS_FIELD).setText(entry.getContents()))
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

    public List<Entry> searchEntries(String query) {
        if (query == null)
        {
            query = "";
        }

        return createEntries(getIndex().search(Query.newBuilder().setOptions(getQueryOptions()).build(query)).getResults());
    }

    public EntryDetails getEntryDetails(Entry entry)
    {
        if (!entry.isPersisted())
        {
            throw new IllegalArgumentException("Entry is not yet persisted");
        }

        String contents = getIndex().get(entry.getDocumentId()).getOnlyField(CONTENTS_FIELD).getText();


        return new EntryDetails(entry, contents);
    }

    private List<Entry> createEntries(Collection<ScoredDocument> documents) {
        ArrayList<Entry> result = new ArrayList<>(documents.size());

        for (ScoredDocument document : documents) {
            result.add(createEntry(document));
        }

        return result;
    }

    private Entry createEntry(ScoredDocument document) {
        String header = document.getOnlyField(HEADER_FIELD).getText();
        Date timestamp = document.getOnlyField(TIMESTAMP_FIELD).getDate();

        Entry result = new Entry(header, timestamp);
        result.setDocumentId(document.getId());

        return result;
    }
}
