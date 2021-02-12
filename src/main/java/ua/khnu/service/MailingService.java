package ua.khnu.service;

import ua.khnu.Bot;
import ua.khnu.dto.MessageForQueue;

import java.time.DayOfWeek;
import java.util.List;

public interface MailingService {

    void setBot(Bot bot);

    void sendClassNotifications(int periodIndex, DayOfWeek dayOfWeek);

    boolean isReady();

    void sendMailingMessages(List<MessageForQueue> messages);
}
