package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.Bot;
import ua.khnu.BotInitializer;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.Period;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.worker.SendNotificationWorker;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SendMessageService {
    private static final int TEN_MINUTES_IN_MILLIS = 600000;
    private final Bot bot;
    private final ScheduleContainer scheduleContainer;
    private final PeriodRepository periodRepository;
    private final SubscriptionService subscriptionService;
    private boolean nextDay;

    @Autowired
    public SendMessageService(Bot bot, ScheduleContainer scheduleContainer,
                              PeriodRepository periodRepository, SubscriptionService subscriptionService) {
        this.bot = bot;
        this.scheduleContainer = scheduleContainer;
        this.periodRepository = periodRepository;
        this.subscriptionService = subscriptionService;
    }

    public boolean isReady() {
        return !scheduleContainer.getSchedule().isEmpty();
    }

    public void performMailing() throws InterruptedException {
        List<Period> currentClasses = getCurrentClasses();
        sendNotifications(currentClasses);
    }

    private List<Period> getCurrentClasses() throws InterruptedException {
        nextDay = false;
        ScheduleUnit nearest = getNearest();
        LocalDateTime now = LocalDateTime.now(ZoneId.of(BotInitializer.TIME_ZONE_ID));
        LocalDateTime startLocalDateTime = nearest.getStartLocalDateTime(nextDay);
        Thread.sleep(
                ChronoUnit.MILLIS.between(now, startLocalDateTime) - TEN_MINUTES_IN_MILLIS
        );
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        return periodRepository.getPeriodByDayAndIndex(nearest.getIndex(), dayOfWeek);
    }

    private void sendNotifications(List<Period> classes) {
        for (Period period : classes) {
            Thread thread =
                    new Thread(
                            new SendNotificationWorker(bot, subscriptionService, period)
                    );
            thread.start();
        }
    }

    private ScheduleUnit getNearest() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(BotInitializer.TIME_ZONE_ID));
        ScheduleUnit nearest = null;
        long min = Long.MAX_VALUE;
        List<ScheduleUnit> schedule = scheduleContainer.getSchedule();
        for (ScheduleUnit scheduleUnit : schedule) {
            long between = ChronoUnit.MILLIS.between(now, scheduleUnit.getStartLocalDateTime());
            if (between <= TEN_MINUTES_IN_MILLIS) {
                continue;
            }
            if (between < min) {
                min = between;
                nearest = scheduleUnit;
            }
        }
        if (nearest == null) {
            nearest = schedule.stream()
                    .filter(s -> s.getIndex() == 1)
                    .findAny()
                    .orElseThrow(IllegalStateException::new);
            nextDay = true;
        }
        return nearest;
    }
}