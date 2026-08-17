package com.atomgdx.ai;

import java.io.Serializable;

/**
 * Message object for conversations with the AtomGdx AI Copilot.
 */
public class AiMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        USER,
        ASSISTANT,
        SYSTEM
    }

    private final Role role;
    private final String content;
    private final long timestamp;

    public AiMessage(Role role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public Role getRole() { return role; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + role + "] " + content;
    }
}
