package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.ChatMediator;

public class LinkReceiver {
    private final ChatMediator chatMediator;

    public LinkReceiver(ChatMediator chatMediator) {
        this.chatMediator = chatMediator;
    }

    public boolean link(String groupId, User user) {
        return chatMediator.linkUser(groupId, user);
    }

    public boolean unlink(String groupId, User user) {
        return chatMediator.unlinkUser(groupId, user);
    }
}