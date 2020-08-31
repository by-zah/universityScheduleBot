package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.User;
import ua.khnu.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createOrUpdate(int userId, long chatId) {
        Optional<User> userOpt = userRepository.getById(userId);
        if (!userOpt.isPresent() || userOpt.get().getChatId() != chatId) {
            User user = new User();
            user.setId(userId);
            user.setChatId(chatId);
            userRepository.createOrUpdate(user);
        }
    }
}
