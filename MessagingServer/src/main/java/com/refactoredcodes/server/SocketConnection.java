package com.refactoredcodes.server;

import java.net.Socket;

public record SocketConnection(Socket socket, long id) { }
