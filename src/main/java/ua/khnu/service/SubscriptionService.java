package ua.khnu.service;

public interface SubscriptionService {

    void subscribe(long userChatId, String groupName);

    void unSubscribe(long userChatId, String groupName);

}
