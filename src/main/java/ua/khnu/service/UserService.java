package ua.khnu.service;

import ua.khnu.entity.User;
import ua.khnu.entity.UserSettings;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void createOrUpdate(int userId, long chatId);

    Optional<User> getUserById(int userId);

    List<User> getAllUsers();

    void updateUser(User user);

    UserSettings switchClassNotificationSetting(int userId);

    UserSettings switchDeadlineNotificationSetting(int userId);
}
