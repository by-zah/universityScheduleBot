package ua.khnu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.User;
import ua.khnu.entity.UserSettings;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createOrUpdate(long userId, long chatId) {
        //TODO needs to be reviewed;
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getChatId() != chatId) {
            User user = new User();
            user.setId(userId);
            user.setChatId(chatId);
            userRepository.save(user);
        }
    }

    @Override
    public User createUser(long userId, long chatId) {
        var user = new User();
        user.setId(userId);
        user.setChatId(chatId);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserSettings switchClassNotificationSetting(long userId) {
        return switchSetting(userId, UserSettings::setClassNotificationsEnabled, UserSettings::isClassNotificationsEnabled);
    }

    @Override
    @Transactional
    public UserSettings switchDeadlineNotificationSetting(long userId) {
        return switchSetting(userId, UserSettings::setDeadlineNotificationsEnabled, UserSettings::isDeadlineNotificationsEnabled);
    }

    private UserSettings switchSetting(long userId, BiConsumer<UserSettings, Boolean> setter, Predicate<UserSettings> getter) {
        final var user = userRepository.findById(userId).orElseGet(() -> new User(userId));
        final var userSettings = user.getSettings();
        setter.accept(userSettings, !getter.test(userSettings));
        userRepository.save(user);
        return user.getSettings();
    }
}
