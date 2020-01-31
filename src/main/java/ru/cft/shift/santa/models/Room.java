package ru.cft.shift.santa.models;

import java.util.List;

public class Room {
    private List<User> users;

    private String name;

    private String id;

    private int capacity;

    private int size;

    public Room(String name, String id, int capacity, int size, List<User> users) {
        this.users = users;
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.size = size;
    }

    public Room() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
