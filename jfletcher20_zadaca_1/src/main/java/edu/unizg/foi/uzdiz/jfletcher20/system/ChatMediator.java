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
            String users = groupUsers.get(groupId).toString();
            users = users.length() > 2 ? users.substring(1, users.length() - 1) : "-";
            groupChats.add(List.of(groupId, users));
        }
        return groupChats;
    }

    public void linkUser(String groupId, User user) {
        groupUsers.computeIfAbsent(groupId, k -> new HashSet<>()).add(user);
        Logs.o(user + " sada participira u grupi " + groupId);
    }

    public void unlinkUser(String groupId, User user) {
        Set<User> users = groupUsers.get(groupId);
        if (users != null && !users.isEmpty() && users.contains(user)) {
            users.remove(user);
            if (users.isEmpty())
                groupUsers.remove(groupId);
                Logs.o(user + " više nije u grupi " + groupId);
        } else {
            Logs.o(user + " nije u grupi " + groupId + " pa se ne mora izbaciti.");
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
                Logs.o(user + " prima poruku \"" + message + "\" od " + sender);
            }
        }
    }
}
