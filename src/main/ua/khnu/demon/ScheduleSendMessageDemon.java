package ua.khnu.demon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.khnu.service.IsDayOffService;
import ua.khnu.service.MailingService;
import ua.khnu.service.ScheduleService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static ua.khnu.util.Constants.TEN_MINUTES_IN_MILLIS;
import static ua.khnu.util.Constants.TIME_ZONE_ID;


public class ScheduleSendMessageDemon implements Runnable {
    private static final Logger LOG = LogManager.getLogger(ScheduleSendMessageDemon.class);
    private final MailingService mailingService;
    private final ScheduleService scheduleService;
    private final IsDayOffService isDayOffService;

    public ScheduleSendMessageDemon(MailingService mailingService, ScheduleService scheduleService,
                                    IsDayOffService isDayOffService) {
        this.mailingService = mailingService;
        this.scheduleService = scheduleService;
        this.isDayOffService = isDayOffService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (!mailingService.isReady()) {
                    Thread.sleep(Long.MAX_VALUE);
                }
                var nextClassStartOpt = scheduleService.getNearest();
                var nextClassStart = nextClassStartOpt.orElseGet(scheduleService::getFirstClassTime);
                var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
                Thread.sleep(
                        ChronoUnit.MILLIS.between(now, nextClassStart.getStartLocalDateTime(nextClassStartOpt.isEmpty())) - TEN_MINUTES_IN_MILLIS
                );
                if (isDayOffService.isTodayDayOf()) {
                    Thread.sleep(90000);
                    continue;
                }
                var dayOfWeek = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).getDayOfWeek();
                mailingService.sendClassNotifications(nextClassStart.getIndex(), dayOfWeek);
                LOG.info("send notification to all students");
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                LOG.info("new schedule set");
            }
        }
    }


}
