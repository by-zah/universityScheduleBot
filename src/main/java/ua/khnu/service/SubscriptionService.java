package ua.khnu.service;

public interface SubscriptionService {

    void subscribe(long userChatId, String message);

    void unSubscribe(long userChatId, String message);

}
