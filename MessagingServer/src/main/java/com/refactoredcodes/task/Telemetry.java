package com.refactoredcodes.task;

import com.refactoredcodes.server.SocketConnectionQueue;

import java.util.TimerTask;

public class Telemetry extends TimerTask {

    private SocketConnectionQueue connectionQueue;

    public Telemetry(final SocketConnectionQueue connectionQueue){
        this.connectionQueue = connectionQueue;
    }
    @Override
    public void run() {
        System.out.println(
                "Total Number Of Active Connections : " +
            connectionQueue.activeConnections()
        );
    }
}
