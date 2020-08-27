package ua.khnu.demon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.khnu.Bot;
import ua.khnu.BotInitializer;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.Group;
import ua.khnu.entity.ScheduleUnit;
import ua.khnu.service.GroupService;
import ua.khnu.service.PeriodService;
import ua.khnu.service.SubscriptionService;
import ua.khnu.worker.SendNotificationWorker;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class ScheduleSendMessageDemon implements Runnable {
    private static final int TEN_MINUTES_IN_MILLIS = 600000;
    private static final Logger LOG = LogManager.getLogger(ScheduleSendMessageDemon.class);
    private final Bot bot;
    private final ScheduleContainer scheduleContainer;
    private final PeriodService periodService;
    private final SubscriptionService subscriptionService;
    private final GroupService groupService;

    public ScheduleSendMessageDemon(Bot bot, ScheduleContainer scheduleContainer,
                                    PeriodService periodService, SubscriptionService subscriptionService,
                                    GroupService groupService) {
        this.bot = bot;
        this.scheduleContainer = scheduleContainer;
        this.periodService = periodService;
        this.subscriptionService = subscriptionService;
        this.groupService = groupService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (scheduleContainer.getSchedule().isEmpty()) {
                    Thread.sleep(Long.MAX_VALUE);
                    continue;
                }
                ScheduleUnit nearest = getNearest();
                LocalDateTime now = LocalDateTime.now(ZoneId.of(BotInitializer.TIME_ZONE_ID));
                Thread.sleep(ChronoUnit.MILLIS.between(now, nearest.getStartLocalDateTime()) - TEN_MINUTES_IN_MILLIS);
                DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
                List<Group> groups = groupService.findGroupsThatHaveSpecifiedPeriod(nearest.getIndex(), dayOfWeek);
                for (Group group : groups) {
                    Thread thread =
                            new Thread(
                                    new SendNotificationWorker(bot, subscriptionService,
                                            group, periodService, nearest.getIndex(), dayOfWeek)
                            );
                    thread.start();
                }
                LOG.info("send notification to all students");
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                LOG.info("new schedule set");
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    private ScheduleUnit getNearest() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(BotInitializer.TIME_ZONE_ID));
        ScheduleUnit nearest = null;
        long min = Long.MAX_VALUE;
        for (ScheduleUnit scheduleUnit : scheduleContainer.getSchedule()) {
            long between = ChronoUnit.MILLIS.between(now, scheduleUnit.getStartLocalDateTime());
            if (between < 0){
                continue;
            }
            if(between < min){
                min = between;
                nearest = scheduleUnit;
            }
        }
        return nearest;
    }

}
