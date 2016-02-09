package com.example.osulld13.digitalcollections;

import java.net.URL;
import java.util.concurrent.Callable;

/**
 * Created by osulld13 on 07/02/16.
 */
public class Request implements Callable<Response> {
    private URL url;

    public Request(URL url) {
        this.url = url;
    }

    @Override
    public Response call() throws Exception {
        return new Response(url.openStream());
    }
}