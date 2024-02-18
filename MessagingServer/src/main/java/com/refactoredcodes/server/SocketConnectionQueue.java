package com.refactoredcodes.server;

import java.util.HashMap;
import java.util.Map;

public class SocketConnectionQueue {
    private long totalConnections = 0;
    private Map<Long, SocketConnection> connectionMap = new HashMap<>();

    public void addConnection(final SocketConnection socketConnection){
        ++totalConnections;
        System.out.println("New Connection Added :- Total Number of connection " + totalConnections);
        connectionMap.put(socketConnection.id(), socketConnection);
    }

    public void removeConnection(final long id){
        --totalConnections;
        System.out.println("Old Connection Removed :- Total Number of Connection "+ totalConnections);
        connectionMap.remove(id);
    }

    public long activeConnections(){
        return totalConnections;
    }
}
