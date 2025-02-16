package es.codeurjc.test;

import java.util.List;

public class Chat {
    
    private List<User> users;

    public Chat(List<User> users) {
        this.users = users;
    }

    public User getUser(String name) {
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    
}
