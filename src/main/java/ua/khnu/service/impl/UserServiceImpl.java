package ua.khnu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.User;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void createOrUpdate(int userId, long chatId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getChatId() != chatId) {
            User user = new User();
            user.setId(userId);
            user.setChatId(chatId);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
