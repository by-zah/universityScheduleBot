package ua.khnu.service;

import ua.khnu.entity.User;
import ua.khnu.entity.UserSettings;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void createOrUpdate(long userId, long chatId);

    User createUser(long userId, long chatId);

    Optional<User> getUserById(long userId);

    List<User> getAllUsers();

    void updateUser(User user);

    UserSettings switchClassNotificationSetting(long userId);

    UserSettings switchDeadlineNotificationSetting(long userId);
}
