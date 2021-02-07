package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.User;
import ua.khnu.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createOrUpdate(int userId, long chatId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getChatId() != chatId) {
            User user = new User();
            user.setId(userId);
            user.setChatId(chatId);
            userRepository.save(user);
        }
    }

    @Transactional
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
