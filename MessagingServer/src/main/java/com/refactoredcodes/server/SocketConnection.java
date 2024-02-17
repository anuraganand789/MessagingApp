package com.refactoredcodes.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public record SocketConnection(Socket socket, long id) { }
