package com.refactoredcodes;

import java.io.BufferedWriter;
import java.io.IOException;

public class SocketUtility {
    public static void writeLineAndFlush(final BufferedWriter writer, final String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
    public static void writeLine(final BufferedWriter writer, final String message) throws IOException {
        writer.write(message);
        writer.newLine();
    }
}
