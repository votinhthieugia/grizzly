package com.athena.base.web;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BaseServer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(BaseServer.class);

    protected String name;
    protected int port;
    protected HttpServer httpServer;
    protected ConnectionProbeController connnectionProbeController;

    public BaseServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public void setup() {
        setupHttpServer();
    }

    public void launch() {
        try {
            httpServer.start();
        } catch (IOException exception) {
            log.error("Error start server:" + exception.getStackTrace());
            System.exit(10000);
        }
    }

    public void shutdown() {

    }

    public void shutdownGracefully() {

    }

    public void relaunch() {

    }

    protected void setupHttpServer() {
        httpServer = HttpServer.createSimpleServer(".", port);

        NetworkListener listener = httpServer.getListener(name);
        TCPNIOTransport transport = listener.getTransport();
        transport.getConnectionMonitoringConfig().addProbes(connnectionProbeController);

        ServerConfiguration configuration = httpServer.getServerConfiguration();
        setupRouters(configuration);
    }

    protected abstract void setupRouters(ServerConfiguration configuration);

}