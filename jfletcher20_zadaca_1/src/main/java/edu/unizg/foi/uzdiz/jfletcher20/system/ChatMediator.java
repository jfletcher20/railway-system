package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public class ChatMediator {
    private final Map<String, Set<User>> groupUsers = new HashMap<String, Set<User>>();

    public List<List<String>> groupChats() {
        List<List<String>> groupChats = new ArrayList<List<String>>();
        for (String groupId : groupUsers.keySet()) {
            List<String> groupChat = new ArrayList<String>();
            groupChat.add(groupId);
            for (User user : groupUsers.get(groupId)) {
                groupChat.add(user.toString());
            }
            groupChats.add(groupChat);
        }
        return groupChats;
    }

    public void linkUser(String groupId, User user) {
        groupUsers.computeIfAbsent(groupId, k -> new HashSet<>()).add(user);
    }

    public void unlinkUser(String groupId, User user) {
        Set<User> users = groupUsers.get(groupId);
        if (users != null) {
            users.remove(user);
            if (users.isEmpty()) {
                groupUsers.remove(groupId);
            }
        }
    }

    public void broadcast(String groupId, User sender, String message) {
        Set<User> users = groupUsers.get(groupId);
        if (users == null || users.isEmpty()) {
            Logs.o("Grupa " + groupId + " nema korisnika koji bi primili poruku.");
            return;
        }
        for (User user : users) {
            if (!user.equals(sender)) {
                System.out.println(user + " prima poruku \"" + message + "\" od " + sender);
            }
        }
    }
}
