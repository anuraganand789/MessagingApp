package com.refactoredcodes.server;

import java.io.*;

public class SocketWorker implements Runnable {
    private final SocketConnection socketConnection;
    private final SocketQueue connectionQueue ;

    public SocketWorker(
            final SocketConnection socketConnection,
            final SocketQueue connectionQueue
    ) {
        this.socketConnection = socketConnection;
        this.connectionQueue = connectionQueue;
    }

    public void work() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try(
                final OutputStream  outputStream    = socketConnection.socket().getOutputStream();

                final InputStream   inputStream     = socketConnection.socket().getInputStream();
                ) {
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            final BufferedReader        bufferedReader  = new BufferedReader(new InputStreamReader(inputStream)) ;
            while (true) {
                final String line = bufferedReader.readLine();
                if(line == null) continue;

                if(line.equalsIgnoreCase("bye")) {
                    socketConnection.socket().close();
                    connectionQueue.removeConnection(socketConnection.id());
                    System.out.printf("Connection stopped with : Socket : [%d].%n", socketConnection.id());
                    break;
                }

                System.out.printf("Socket [%d] : %s .%n",  socketConnection.id(), line);
                bufferedWriter.write("Received ");
                bufferedWriter.write(line);
                bufferedWriter.write(System.lineSeparator());
            }
        }catch (final IOException ioException) {
            ioException.printStackTrace();
        }

    }
}
