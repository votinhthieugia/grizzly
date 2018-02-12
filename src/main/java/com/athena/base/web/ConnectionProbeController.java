package com.athena.base.web;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.ConnectionProbe;
import org.glassfish.grizzly.nio.transport.TCPNIOServerConnection;
import org.glassfish.grizzly.nio.transport.UDPNIOServerConnection;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionProbeController extends ConnectionProbe.Adapter {
    private AtomicInteger requestCount = new AtomicInteger(0);
    private AtomicInteger connectionCount = new AtomicInteger(0);

    public void onAcceptEvent(Connection serverConnection, Connection clientConnection) {
        assert((serverConnection instanceof TCPNIOServerConnection) || (serverConnection instanceof UDPNIOServerConnection));
        requestCount.incrementAndGet();
        connectionCount.incrementAndGet();
    }

    public void onCloseEvent(Connection connection) {
        assert((connection instanceof TCPNIOServerConnection) || (connection instanceof UDPNIOServerConnection));
        connectionCount.decrementAndGet();
    }

    public Integer getRequestCount() {
        return requestCount.get();
    }

    public Integer getConnectionCount() {
        return connectionCount.get();
    }
}
