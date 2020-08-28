package ua.khnu.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.khnu.Bot;
import ua.khnu.entity.Period;
import ua.khnu.entity.Subscription;
import ua.khnu.service.SubscriptionService;

import java.util.List;

public class SendNotificationWorker implements Runnable {
    private static final Logger LOG = LogManager.getLogger(SendNotificationWorker.class);

    private final Bot bot;
    private final List<Subscription> subscriptions;
    private final Period period;

    public SendNotificationWorker(Bot bot, SubscriptionService subscriptionService,
                                  Period period) {
        this.bot = bot;
        this.subscriptions = subscriptionService.getAllSubscriptionsByGroupName(period.getGroupName());
        this.period = period;
    }

    @Override
    public void run() {
        String draft = period.getName() + " in 10 minutes in room " + period.getRoomNumber();
        String message = period.getBuilding() == null ? draft : draft + " in " + period.getBuilding() + " building";
        subscriptions.forEach(x -> {
            try {
                bot.sendMessage(message, x.getUser());
            } catch (TelegramApiException e) {
                LOG.error(e);
            }
        });
    }

}
