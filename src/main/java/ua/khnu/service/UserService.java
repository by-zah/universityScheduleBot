package ua.khnu.service;

import ua.khnu.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void createOrUpdate(int userId, long chatId);

    Optional<User> getUserById(int userId);

    List<User> getAllUsers();

}
