package ru.cft.shift.santa.repositories;

import ru.cft.shift.santa.models.User;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс для получения данных по книгам
 */
public interface UserRepository {
    User fetchUser(String userId);
    User createUser(User user);
    List<User> getAllUsers();
    void appointRecipient(String userId, String recipientId);
}
