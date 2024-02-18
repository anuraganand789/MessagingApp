package com.refactoredcodes.chat;

import com.refactoredcodes.GlobalState;
import com.refactoredcodes.server.SocketWorker;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MessageQueue {
    // put the messages in this queue to write it to the message file
    private final Queue<Message> writerQueue = new ArrayDeque<>();

    // put the messages in this queue to send the message to the target user
    private final Queue<Message> readerQueue = new ArrayDeque<>();

    public void push(final Message message) {
        writerQueue.offer(message);
        readerQueue.offer(message);
    }

    public void readReaderQueue(){
        final TimerTask readerQueueTask = new TimerTask() {
            @Override
            public void run() {

                while(!readerQueue.isEmpty()) {
                    final Message message = readerQueue.poll();

                    System.out.println("read message from queue" + message);

                    if(message.fromUser() == null || message.toUser() == null) continue;

                    final User sender       = GlobalState.userMap.get(message.fromUser().name());
                    final User currentUser  = GlobalState.userMap.get(message.toUser().name());

                    final SocketWorker socketWorker = GlobalState.userToWorkerMap.get(currentUser);

                    if(socketWorker == null && currentUser != null) {
                        GlobalState.userToWorkerMap.put(currentUser, new SocketWorker());
                        GlobalState.userToWorkerMap.get(sender).pushMessage(
                                new Message(
                                        new User(
                                                "System Notifier", ""
                                        ),
                                        sender,
                                        String.format("Currently, %s is not online, He will receive the message once online!!!", currentUser.name()),
                                        System.currentTimeMillis()
                                )
                        );
                    }
                    GlobalState.userToWorkerMap.get(currentUser).pushMessage(message);
                }
            }
        };

        System.out.println(
                "Setting scheduled task for reading messaging queue"
        );
        new Timer().scheduleAtFixedRate(readerQueueTask, 1000, 1000);
    }
}
