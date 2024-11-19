package com.brettstine.social_game_backend.utils;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class MessageQueue {
    private final WebSocketSession session;
    private final ConcurrentLinkedQueue<TextMessage> queue = new ConcurrentLinkedQueue<>();
    private boolean isSending = false;

    public MessageQueue(WebSocketSession session) {
        this.session = session;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public synchronized void enqueue(TextMessage message) {
        queue.add(message);
        if (!isSending) {
            sendNext();
        }
    }

    private synchronized void sendNext() {
        if (queue.isEmpty()) {
            isSending = false;
            return;
        }
        isSending = true;
        TextMessage message = queue.poll();

        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
            sendNext(); // Send the next message recursively after the current one
        } catch (IOException e) {
            isSending = false; // Stop sending on error
        }
    }
}

