package ua.khnu.demon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.service.DeadlineService;
import ua.khnu.service.MailingService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Component
public class DeadlineSendMessageDemon implements Runnable {
    private static final Object MONITOR = new Object();
    private final MailingService mailingService;
    private final DeadlineService deadlineService;
    private Optional<DeadlineNotificationDto> nextDeadlineNotification;

    @Autowired
    public DeadlineSendMessageDemon(MailingService mailingService, DeadlineService deadlineService) {
        this.mailingService = mailingService;
        this.deadlineService = deadlineService;
        nextDeadlineNotification = Optional.empty();
        deadlineService.setDeadlineSendMessageDemon(this);
        Executors.newSingleThreadExecutor().execute(this);
    }

    @Override
    public void run() {
        //TODO wait to
        while (true) {
            try {
                nextDeadlineNotification = deadlineService.getNextDeadlineToNotification();
                while (nextDeadlineNotification.isEmpty()) {
                    synchronized (this) {
                        wait(Long.MAX_VALUE);
                    }
                }
                var nextDeadlineTemp = nextDeadlineNotification.get();
                var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
                synchronized (this) {
                    wait(ChronoUnit.MILLIS.between(now, nextDeadlineTemp.getDeadline().getDeadLineTime()) - nextDeadlineTemp.getMillis());
                }
                if (nextDeadlineNotification.isEmpty() || !Objects.equals(nextDeadlineNotification.get(), nextDeadlineTemp)) {
                    continue;
                }
                mailingService.sendDeadlineNotifications(nextDeadlineTemp.getDeadline());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void onNewDeadlineAdded() {
        nextDeadlineNotification = Optional.empty();
        //not notifying
        synchronized (this) {
            notifyAll();
        }
    }

}
