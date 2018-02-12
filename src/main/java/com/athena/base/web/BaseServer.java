package com.athena.base.web;

import org.glassfish.grizzly.http.server.HttpServer;

public abstract class BaseServer extends Thread {
    protected int port;
    protected HttpServer httpServer;
}
