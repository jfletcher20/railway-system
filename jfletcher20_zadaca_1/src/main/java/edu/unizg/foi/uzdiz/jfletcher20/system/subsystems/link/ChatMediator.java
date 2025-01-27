package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history.LinkCommand;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history.LinkInvoker;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history.LinkReceiver;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history.UnlinkCommand;

public class ChatMediator {
    private final Map<String, Set<User>> groupUsers = new HashMap<String, Set<User>>();
    private final LinkInvoker linkInvoker = new LinkInvoker();
    private final LinkReceiver linkReceiver = new LinkReceiver(this);

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

    public List<String> chatsUserIsIn(User user) {
        List<String> chats = new ArrayList<String>();
        for (String groupId : groupUsers.keySet()) {
            Set<User> users = groupUsers.get(groupId);
            if (users.contains(user)) {
                chats.add(groupId);
            }
        }
        return chats;
    }

    public boolean linkUser(String groupId, User user) {
        if (groupUsers.containsKey(groupId) && groupUsers.get(groupId).contains(user)) {
            Logs.uw(user, "je već u grupi " + groupId);
            return false;
        }
        groupUsers.computeIfAbsent(groupId, k -> new HashSet<>()).add(user);
        logLink(user, groupId);
        return true;
    }

    public static void logLink(User user, String groupId) {
        Logs.uw(user, "sada participira u grupi " + groupId);
    }

    public boolean unlinkUser(String groupId, User user) {
        Set<User> users = groupUsers.get(groupId);
        if (users != null && !users.isEmpty() && users.contains(user)) {
            users.remove(user);
            if (users.isEmpty())
                groupUsers.remove(groupId);
            Logs.uw(user, "više nije u grupi " + groupId);
            return true;
        } else {
            Logs.uw(user, "nije u grupi " + groupId + " pa se ne mora izbaciti");
            return false;
        }
    }

    public static void logUnlink(User user, String groupId) {
        Logs.uw(user, "više nije u grupi " + groupId);
    }

    public void broadcast(String groupId, User sender, String message) {
        Set<User> users = groupUsers.get(groupId);
        if (users == null || users.isEmpty()) {
            Logs.uw(sender, "grupa " + groupId + " nema korisnika koji bi primili poruku");
            return;
        }
        for (User user : users) {
            if (!user.equals(sender)) {
                Logs.u(user, sender, message);
            }
        }
    }

    public void broadcast(String groupId, User sender, String message, boolean checkIfEmpty) {
        Set<User> users = groupUsers.get(groupId);
        if (users == null || users.isEmpty()) {
            if (!checkIfEmpty)
                Logs.uw(sender, "grupa " + groupId + " nema korisnika koji bi primili poruku");
            return;
        }
        for (User user : users) {
            if (!user.equals(sender)) {
                Logs.u(user, sender, message);
            }
        }
    }

    public void printHistory() {
        linkInvoker.showHistory();
    }

    public void undoLastCommand() {
        linkInvoker.undoLast();
    }

    public void undoAllCommands() {
        linkInvoker.undoAll();
    }
    public void command(String groupId, User user, boolean isLink) {
        if (isLink) linkInvoker.executeCommand(new LinkCommand(linkReceiver, groupId, user));
        else linkInvoker.executeCommand(new UnlinkCommand(linkReceiver, groupId, user));
    }
}
