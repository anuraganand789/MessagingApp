package com.refactoredcodes.server;

import com.refactoredcodes.GlobalState;
import com.refactoredcodes.SocketUtility;
import com.refactoredcodes.chat.Message;
import com.refactoredcodes.chat.User;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Queue;

public class SocketWorker {
    private volatile boolean connectionClosed = false;

    private final SocketConnection      socketConnection;
    private final SocketConnectionQueue connectionQueue ;
    private final Queue<Message>        newMessageQueue;

    private BufferedReader  bufferedReader;
    private BufferedWriter  bufferedWriter;

    private User loggedInUser;

    public SocketWorker () {
        this(null, null);
    }
    public SocketWorker(
            final SocketConnection socketConnection,
            final SocketConnectionQueue connectionQueue
    ) {
        this.socketConnection   = socketConnection;
        this.connectionQueue    = connectionQueue;
        this.newMessageQueue    =   new ArrayDeque<>();
    }

    public void work() {
        new Thread(this::readFromClient).start();
        new Thread(this::writeToClient).start();
    }

    private void readFromClient(){
        System.out.println("In the read from client method");
        try(
                final InputStream   inputStream     = socketConnection.socket().getInputStream();
        ) {

            this.bufferedReader  = new BufferedReader(new InputStreamReader(inputStream)) ;

            final String credentials = bufferedReader.readLine();
            System.out.println("Credentails received form client " + credentials);

            final String[] uidPwd = credentials.split(":");
            if(uidPwd.length < 2) {
                refuseConnection("Invalid Credentails Format");
                return;
            }
            if(!GlobalState.userMap.containsKey(uidPwd[0])) {
                refuseConnection("User Not Found.");
                return;
            }
            final User currentUser = GlobalState.userMap.get(uidPwd[0]);
            if(!currentUser.password().equalsIgnoreCase(uidPwd[1])) {
                refuseConnection("Authentication Failed.");
                return;
            }

            if(GlobalState.userToWorkerMap.containsKey(currentUser)) {
                final SocketWorker worker = GlobalState.userToWorkerMap.get(currentUser);
                this.newMessageQueue.addAll(worker.newMessageQueue);
            }

            GlobalState.userToWorkerMap.put(currentUser, this);

            loggedInUser = currentUser;

            while (!this.connectionClosed) {
                final String line = bufferedReader.readLine();
                if(line.equalsIgnoreCase("$$")) {
                    handleNewMessage(bufferedReader);
                } else if(line.equalsIgnoreCase("bye")) {
                    goodbye();
                    this.connectionClosed = true;
                    break;
                }
            }
        }catch (final IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void pushMessage(final Message message) {
        this.newMessageQueue.offer(message);
        System.out.println("new message inserted " + message);
    }

    private void writeToClient(){
        try(
                final OutputStream  outputStream    = socketConnection.socket().getOutputStream();
        ) {

            this.bufferedWriter  = new BufferedWriter(new OutputStreamWriter(outputStream));

            SocketUtility.writeLineAndFlush(bufferedWriter, "Send Credentials");

            while (!this.connectionClosed) {
                if (!newMessageQueue.isEmpty()) {
                    final Message message = newMessageQueue.poll();
                    System.out.println("sending new message to client " + message);

                    SocketUtility.writeLine(bufferedWriter, "$$");
                    SocketUtility.writeLine(bufferedWriter, message.fromUser().name());
                    SocketUtility.writeLine(bufferedWriter, message.toUser().name());
                    SocketUtility.writeLine(bufferedWriter, message.timestamp() + "");
                    SocketUtility.writeLine(bufferedWriter, message.message());
                    SocketUtility.writeLineAndFlush(bufferedWriter, "$$");
                }
            }
        }catch (final IOException ioException) {
            ioException.printStackTrace();
        }finally {
            try {
                disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
//    @Override
//    public void run() {
//        System.out.println("In the run method");
//        try(
//                final OutputStream  outputStream    = socketConnection.socket().getOutputStream();
//                final InputStream   inputStream     = socketConnection.socket().getInputStream();
//                ) {
//
//            this.bufferedWriter  = new BufferedWriter(new OutputStreamWriter(outputStream));
//            this.bufferedReader  = new BufferedReader(new InputStreamReader(inputStream)) ;
//
//            bufferedWriter.write("Send Credential");
//            bufferedWriter.flush();
//
//            final String credentials = bufferedReader.readLine();
//            System.out.println("Credentails received form client " + credentials);
//            final String[] uidPwd = credentials.split(":");
//            if(uidPwd.length < 2) {
//                refuseConnection("Invalid Credentails Format");
//                return;
//            }
//            if(!GlobalState.userMap.containsKey(uidPwd[0])) {
//                refuseConnection("User Not Found.");
//                return;
//            }
//            if(!GlobalState.userMap.get(uidPwd[0]).password().equalsIgnoreCase(uidPwd[1])) {
//                refuseConnection("Authentication Failed.");
//                return;
//            }
//            bufferedWriter.write("Login Successful");
//            while (true) {
//                final String line = bufferedReader.readLine();
//                System.out.println("new line from client " + line);
//                if(line != null) {
//                    if(line.equalsIgnoreCase("$$")) {
//                      handleNewMessage(bufferedReader);
//                    } else if(line.equalsIgnoreCase("bye")) {
//                        goodbye();
//                        break;
//                    }
//                }
//
//                final Message message = newMessageQueue.poll();
//
//                if (message != null) {
//                    bufferedWriter.write("$$");
//                    bufferedWriter.write(message.fromUser().name());
//                    bufferedWriter.write(message.toUser().name());
//                    bufferedWriter.write(message.timestamp() + "");
//                    bufferedWriter.write(message.message());
//                    bufferedWriter.write("$$");
//                    bufferedWriter.flush();
//                }
//
//            }
//        }catch (final IOException ioException) {
//            ioException.printStackTrace();
//        }finally {
//            try {
//                disconnect();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//    }

    private void handleNewMessage(final BufferedReader bufferedReader) throws IOException {
        System.out.println("reading new message");
        final String from       = bufferedReader.readLine();
        final String to         = bufferedReader.readLine();
        final long timestamp    = Long.parseLong(bufferedReader.readLine());
        final String message    = bufferedReader.readLine();
        // read end of message
        bufferedReader.readLine();

        final Message newMessage = new Message(
                GlobalState.userMap.get(from),
                GlobalState.userMap.get(to),
                message,
                timestamp
        );

        System.out.println(message);

        GlobalState.messageQueue.push(newMessage);
    }

    private void disconnect() throws IOException {
        socketConnection.socket().close();
        connectionQueue.removeConnection(socketConnection.id());
        GlobalState.userToWorkerMap.remove(loggedInUser);
    }

    private void goodbye() throws IOException {
        SocketUtility.writeLine(bufferedWriter, "Good Bye");
        SocketUtility.writeLineAndFlush(bufferedWriter,"Connection Closed!!");
        disconnect();
    }
    private void refuseConnection(final String reasonToRefuse) throws IOException {
        SocketUtility.writeLineAndFlush(bufferedWriter, reasonToRefuse);
        System.out.println(reasonToRefuse);
        disconnect();
    }
}
