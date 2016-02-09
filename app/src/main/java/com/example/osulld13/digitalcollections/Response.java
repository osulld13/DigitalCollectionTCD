package com.example.osulld13.digitalcollections;

import java.io.InputStream;

/**
 * Created by osulld13 on 07/02/16.
 */
public class Response {
    private InputStream body;

    public Response(InputStream body) {
        this.body = body;
    }

    public InputStream getBody() {
        return body;
    }
}