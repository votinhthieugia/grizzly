package com.athena.base.web;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

public class BaseHandler extends HttpHandler {
    public BaseHandler() {

    }

    @Override
    public void service(final Request request, final Response response) throws Exception {
    }
}
