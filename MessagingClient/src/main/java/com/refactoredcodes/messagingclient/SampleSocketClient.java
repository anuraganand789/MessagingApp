package com.refactoredcodes.messagingclient;

import java.io.*;
import java.net.Socket;

public class SampleSocketClient {
    private static volatile boolean logoff = false;

    static final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException, InterruptedException {
        try(
                final Socket socket = new Socket("localhost", 4999);
                final InputStream inputStream = socket.getInputStream();
                final OutputStream outputStream = socket.getOutputStream();
        ){
            final BufferedWriter    bufferedWriter  =   new BufferedWriter(new OutputStreamWriter(outputStream));
            final BufferedReader    bufferedReader  =   new BufferedReader(new InputStreamReader(inputStream));

            final Thread writerThread = new Thread(() -> {
                try {
                    writeToServer(bufferedWriter);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
//
            final Thread readerThread = new Thread(() -> {
                try {
                    readFromServer(bufferedReader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
//
            writerThread.start();
            readerThread.start();

            writerThread.join();
            readerThread.join();
        }
    }

    private static  void writeToServer(final BufferedWriter writeToServer) throws IOException {

        System.out.println("Enter Credentials");
        final String credential = consoleReader.readLine();
        final String fromUser = credential.split(":")[0];

        writeToServer.write(credential);
        writeToServer.newLine();
        writeToServer.flush();

        while (!logoff) {
            final String message = consoleReader.readLine();
            if (message.startsWith("/message")) {
                final String toUser = message.split(" ")[1];
//                System.out.println("toUser " + toUser);
                final String sendMessage = consoleReader.readLine();
//                System.out.println("send message " + sendMessage);

                writeToServer.write("$$");
                writeToServer.newLine();

                writeToServer.write(fromUser);
                writeToServer.newLine();

                writeToServer.write(toUser);
                writeToServer.newLine();

                writeToServer.write(System.currentTimeMillis() + "");
                writeToServer.newLine();

                writeToServer.write(sendMessage);
                writeToServer.newLine();

                writeToServer.write("$$");
                writeToServer.newLine();

                writeToServer.flush();
//                System.out.println("message written to server");
            } else if (
                    message.equalsIgnoreCase("/quit") ||
                            message.equalsIgnoreCase("bye")
            ) {
                writeToServer.write("bye");
                writeToServer.write(System.lineSeparator());
                writeToServer.flush();
                logoff = true;
            }
        }
    }

    private static void readFromServer(final BufferedReader bufferedReader) throws IOException {
//        boolean messageStarted = false;

        while(!logoff){
            final String line = bufferedReader.readLine();
            if(line.equalsIgnoreCase("$$")) {
                final String fromUser   = bufferedReader.readLine();
                final String toUser     = bufferedReader.readLine();
                final String timestamp  = bufferedReader.readLine();
                final String message    = bufferedReader.readLine();
                bufferedReader.readLine();
                System.out.printf("%s : %s%n", fromUser, message);
            } else {
                System.out.println(line);
            }
        }
    }
}
