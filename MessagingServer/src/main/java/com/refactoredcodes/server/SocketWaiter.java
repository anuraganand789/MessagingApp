package com.refactoredcodes.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketWaiter {
    private int connectionCount = 0;
    private int connectionId    = 0;
    private final ServerSocket serverSocket;
    private final SocketQueue connectionQueue;

    public SocketWaiter(final ServerSocket serverSocket, final SocketQueue connectionQueue) {
        this.serverSocket = serverSocket;
        this.connectionQueue = connectionQueue;
    }

    public void waitForConnection() throws IOException {
        while (true){
            final Socket socket = serverSocket.accept();
            System.out.println("New connection arrived");
            final SocketConnection socketConnection = new SocketConnection(socket, ++connectionId);
            ++connectionCount;
            System.out.println("new connection accepted");
            System.out.println("Total Number Of new connections " + connectionCount);

            connectionQueue.addConnection(socketConnection);
            new SocketWorker(socketConnection, connectionQueue).work();
        }
    }
}
