package ru.cft.shift.santa.models;

public class User {
    private String id;
    private String name;
    private String wishes;
    private User recipient;
    private Room room;
    public User() {}

    public User(String id, String name, String wishes, User recipient, Room room) {
        this.id = id;
        this.name = name;
        this.wishes = wishes;
        this.recipient = recipient;
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWishes() {
        return wishes;
    }

    public void setWishes(String wishes) {
        this.wishes = wishes;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}