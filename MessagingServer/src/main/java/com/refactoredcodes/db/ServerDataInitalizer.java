package com.refactoredcodes.db;

import com.refactoredcodes.GlobalState;
import com.refactoredcodes.chat.Message;
import com.refactoredcodes.chat.Room;
import com.refactoredcodes.chat.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ServerDataInitalizer {

    public void initialize() throws IOException {

        final String userDataFile = "D:/MessageAppData/users.txt";

        System.out.println("Loading users");

        GlobalState.userList.addAll(
            Files.readAllLines(Path.of("d:/MessageAppData", "users.txt"))
                    .stream()
                    .map(line -> line.split(":"))
                    .map(credentialArray -> new User(credentialArray[0], credentialArray[1]))
                    .collect(Collectors.toList())
        );

        System.out.println("List of users loaded");
        for(final User user : GlobalState.userList) {
            GlobalState.userMap.put(user.name(), user);
            System.out.println(user);
        }

        extractMessage();

        for(final Message message : GlobalState.messageList) System.out.println(message);

        final String roomsDataFile = "D:\\MessageAppData\\rooms.txt";
        System.out.println("loading rooms from " + roomsDataFile);

        GlobalState.roomList.addAll(
                Files.readAllLines(Path.of("d:/MessageAppData", "rooms.txt"))
                        .stream()
                        .map(Room::new)
                        .collect(Collectors.toList())
        );

        System.out.println("all rooms loaded");
        for(final Room room : GlobalState.roomList) System.out.println(room);

    }

    private void extractMessage() throws IOException {
        final String messagesDataFile = "D:\\MessageAppData\\messages.txt";
        System.out.println("Loading messages from " + messagesDataFile);

        final FileReader fileReader = new FileReader(messagesDataFile);
        final BufferedReader reader = new BufferedReader(fileReader);

        final List<Message> messageList = GlobalState.messageList;

        while(true){
            final String    fromUser    =   reader.readLine();
            if(fromUser == null) break;
            final String    toUser      =   reader.readLine();
            final String    timestamp   =   reader.readLine();
            final String    message     =   reader.readLine();

            // disacard separator
            reader.readLine();

            messageList.add(
                new Message(GlobalState.userMap.get(fromUser), GlobalState.userMap.get(toUser), message, Long.parseLong(timestamp))
            );
        }

        System.out.println("messages loaded");
    }
}
