package ru.cft.shift.santa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cft.shift.santa.models.User;
import ru.cft.shift.santa.repositories.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User provideUser(String userId) {
        return userRepository.fetchUser(userId);
    }

    public List<User> provideAllUsers() {
        return userRepository.getAllUsers();
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }
}
