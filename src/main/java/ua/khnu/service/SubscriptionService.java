package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Subscription;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.SubscriptionRepository;
import ua.khnu.repository.UserRepository;
import ua.khnu.util.MessageParser;

import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               GroupRepository groupRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public void subscribe(long userChatId, String message) {
        String groupName = MessageParser.getArgumentByPositionAndSeparator(1, " ", message);
        if (!groupRepository.getByGroupName(groupName).isPresent()) {
            throw new BotException("There isn`t group with specified name");
        }
        if (subscriptionRepository.getByUserChatIdAndGroupName(userChatId, groupName).isPresent()) {
            throw new BotException("You have been already subscribed");
        }
        Subscription subscription = new Subscription();
        subscription.setGroup(groupName);
        subscription.setUserChatId(userChatId);
        subscriptionRepository.createOrUpdate(subscription);
    }

    public void unSubscribe(long userChatId, String message) {
        String groupName = MessageParser.getArgumentByPositionAndSeparator(1, " ", message);
        Subscription subscription = new Subscription();
        subscription.setUserChatId(userChatId);
        subscription.setGroup(groupName);
        subscriptionRepository.delete(subscription);
    }

    public List<Subscription> getAllUsersSubscriptions(long userChatId){
        return subscriptionRepository.getAllByUserChatId(userChatId);
    }
}
