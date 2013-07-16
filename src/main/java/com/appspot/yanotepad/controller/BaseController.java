package com.appspot.yanotepad.controller;

import com.google.appengine.api.search.*;
import com.google.appengine.api.users.User;

import java.util.Date;

public class BaseController {
    private final User user;

    private Index index;
    private QueryOptions queryOptions;

    public BaseController(User user) {
        this.user = user;
    }

    protected Index getIndex() {
        if ( index == null )
        {
            index = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(user.getNickname()));
        }

        return index;
    }

    protected QueryOptions getQueryOptions() {
        if ( queryOptions == null )
        {
            queryOptions = QueryOptions.newBuilder()
                    .setFieldsToReturn("header", "timestamp")
                    .setSortOptions(SortOptions.newBuilder()
                            .addSortExpression(SortExpression.newBuilder()
                                    .setExpression("timestamp")
                                    .setDirection(SortExpression.SortDirection.DESCENDING)
                                    .setDefaultValue("1900-01-01")
                            )
                    )
                    .build();
        }

        return queryOptions;
    }

    protected Date now() {
        return new Date();
    }
}