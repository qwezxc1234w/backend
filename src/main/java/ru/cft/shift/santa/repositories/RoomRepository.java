package ru.cft.shift.santa.repositories;

import ru.cft.shift.santa.models.Room;
import ru.cft.shift.santa.models.User;

import java.util.Collection;
import java.util.List;

public interface RoomRepository {
    List<Room> getAllRooms();
    Room fetchRoom(String roomId);
    Room createRoom(Room room);
    void increaseRoomSize(String roomId);
    boolean isRoomFull(String roomId);
}
