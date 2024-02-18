package com.refactoredcodes;

import com.refactoredcodes.chat.Message;
import com.refactoredcodes.chat.MessageQueue;
import com.refactoredcodes.chat.Room;
import com.refactoredcodes.chat.User;
import com.refactoredcodes.server.SocketConnection;
import com.refactoredcodes.server.SocketWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalState {
    public static List<User>                userList    = new ArrayList<>();
    public static Map<String, User>         userMap     = new HashMap<>();
    public static Map<User, SocketWorker> userToWorkerMap = new HashMap<>();

    public static List<Message> messageList =   new ArrayList<>();
    public static List<Room>    roomList    =   new ArrayList<>();

    public static MessageQueue messageQueue = new MessageQueue();
}
