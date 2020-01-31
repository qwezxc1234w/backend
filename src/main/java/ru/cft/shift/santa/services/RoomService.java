package ru.cft.shift.santa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cft.shift.santa.models.Room;
import ru.cft.shift.santa.models.User;
import ru.cft.shift.santa.repositories.RoomRepository;
import ru.cft.shift.santa.repositories.UserRepository;

import java.util.List;
import java.util.Random;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository,
                       UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public Room provideRoom(String roomId) {
        return roomRepository.fetchRoom(roomId);
    }

    public List<Room> provideAllRooms() {
        return roomRepository.getAllRooms();
    }

    public List<User> provideUsersInRoom(String roomId) {
        return roomRepository.getUsersInRoom(roomId);
    }

    public Room createRoom(Room room) {
        return roomRepository.createRoom(room);
    }

    public void addUserInRoom(String roomId, User user) {
        roomRepository.addUserInRoom(roomId, user);
        if (roomRepository.isRoomFull(roomId)) {
            List<User> users = provideUsersInRoom(roomId);
            for (int i = 0; i < users.size(); ++i)
                userRepository.appointRecipient(users.get(i).getId(), users.get((i + 1) % users.size()).getId());
        }
    }
}
