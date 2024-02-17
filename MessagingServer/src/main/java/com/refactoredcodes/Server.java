package com.refactoredcodes;

import com.refactoredcodes.server.SocketQueue;
import com.refactoredcodes.server.SocketWaiter;
import com.refactoredcodes.task.Telemetry;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;

public class Server {

    public static void main( String[] args ) throws IOException {
        System.out.println("Server started");
        final SocketQueue connectionQueue = new SocketQueue();

        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new Telemetry(connectionQueue), 1000, 5000);

        new SocketWaiter(new ServerSocket(4999), connectionQueue).waitForConnection();

        System.out.println("Server closed");
    }
}
