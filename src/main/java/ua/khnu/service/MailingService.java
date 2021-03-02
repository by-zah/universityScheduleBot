package ua.khnu.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.khnu.Bot;
import ua.khnu.entity.Deadline;

import java.time.DayOfWeek;
import java.util.List;

public interface MailingService {

    void setBot(Bot bot);

    void sendClassNotifications(int periodIndex, DayOfWeek dayOfWeek);

    boolean isReady();

    void sendMailingMessages(List<SendMessage> messages);

    void sendDeadlineNotifications(List<Deadline> deadlines);
}
