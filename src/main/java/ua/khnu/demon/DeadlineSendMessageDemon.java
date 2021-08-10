package ua.khnu.demon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.service.DeadlineService;
import ua.khnu.service.MailingService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static ua.khnu.util.Constants.ONE_SECOND_IN_MILLIS;
import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Component
public class DeadlineSendMessageDemon implements Runnable {
    private static final Logger LOG = LogManager.getLogger(DeadlineSendMessageDemon.class);
    private final MailingService mailingService;
    private final DeadlineService deadlineService;
    private List<DeadlineNotificationDto> nextDeadlineNotifications;

    @Autowired
    public DeadlineSendMessageDemon(MailingService mailingService, DeadlineService deadlineService) {
        this.mailingService = mailingService;
        this.deadlineService = deadlineService;
        nextDeadlineNotifications = List.of();
        deadlineService.setDeadlineSendMessageDemon(this);
    }

    @PostConstruct
    private void postConstruct(){
        Executors.newSingleThreadExecutor().execute(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    wait(ONE_SECOND_IN_MILLIS);
                    nextDeadlineNotifications = deadlineService.getNextDeadlineToNotification();
                    while (nextDeadlineNotifications.isEmpty()) {
                        LOG.info("No deadlines, wait for updates");
                        wait(Long.MAX_VALUE);

                    }
                }
                var nextDeadlinesTemp = nextDeadlineNotifications;
                var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
                final var firstDeadline = nextDeadlinesTemp.get(0);
                synchronized (this) {
                    final var timeoutMillis = ChronoUnit.MILLIS.between(now, firstDeadline.getDeadline().getDeadLineTime()) - firstDeadline.getMillis();
                    LOG.info("Thread wait {} millis to next deadline",timeoutMillis);
                    wait(timeoutMillis);
                }
                if (nextDeadlineNotifications.isEmpty() || !Objects.equals(nextDeadlineNotifications, nextDeadlinesTemp)) {
                    continue;
                }
                mailingService.sendDeadlineNotifications(nextDeadlinesTemp.stream()
                        .map(DeadlineNotificationDto::getDeadline)
                        .collect(Collectors.toList()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    public void onNewDeadlineAdded() {
        nextDeadlineNotifications = List.of();
        //not notifying
        synchronized (this) {
            notifyAll();
        }
    }

}
