package com.refactoredcodes.chat;

public record Message(User fromUser, User toUser, String message, long timestamp) {
}
