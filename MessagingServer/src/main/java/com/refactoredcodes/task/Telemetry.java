package com.refactoredcodes.task;

import com.refactoredcodes.server.SocketQueue;

import java.util.TimerTask;

public class Telemetry extends TimerTask {

    private SocketQueue connectionQueue;

    public Telemetry(final SocketQueue connectionQueue){
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
