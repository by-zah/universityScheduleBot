package ua.khnu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Subscription;
import ua.khnu.entity.pk.SubscriptionPK;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.SubscriptionRepository;
import ua.khnu.service.SubscriptionService;
import ua.khnu.util.MessageParser;

import javax.transaction.Transactional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, GroupRepository groupRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    @Transactional
    public void subscribe(long userChatId, String groupName) {
        if (!groupRepository.existsById(groupName)) {
            throw new BotException("There isn`t group with specified name");
        }
        if (subscriptionRepository.existsById(new SubscriptionPK(userChatId, groupName))) {
            throw new BotException("You have been already subscribed");
        }
        Subscription subscription = new Subscription();
        subscription.setGroup(groupName);
        subscription.setUserChatId(userChatId);
        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void unSubscribe(long userChatId, String message) {
        String groupName = MessageParser.getArgumentByPositionAndSeparator(1, " ", message);
        Subscription subscription = new Subscription();
        subscription.setUserChatId(userChatId);
        subscription.setGroup(groupName);
        subscriptionRepository.delete(subscription);
    }

}
