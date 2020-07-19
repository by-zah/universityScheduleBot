package ua.khnu.demon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.khnu.Bot;
import ua.khnu.dto.DayAndIndex;
import ua.khnu.entity.Day;
import ua.khnu.entity.Lesson;
import ua.khnu.service.ScheduleService;
import ua.khnu.util.Converter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ua.khnu.BotInitializer.TIME_ZONE_ID;

@Component
public class ScheduleSendMessageDemon implements Runnable {
    private static final int TEN_MINUTES_IN_MILLIS = 600000;
    private static final Logger LOG = LogManager.getLogger(ScheduleSendMessageDemon.class);
    private final List<Long> subscribers;
    private final Bot bot;
    private final ScheduleService scheduleService;

    private Day stateDay;
    private int stateIndex;

    @Autowired
    public ScheduleSendMessageDemon(List<Long> subscribers, Bot bot, ScheduleService scheduleService) {
        this.subscribers = subscribers;
        this.bot = bot;
        this.scheduleService = scheduleService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!scheduleService.isSchedulePresent()) {
                    Thread.sleep(Long.MAX_VALUE);
                    continue;
                }
                checkState();
                LocalDateTime now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
                Lesson lesson = stateDay.getLessons().get(stateIndex);
                LocalDateTime startLessonDateTime = Converter.lessonToStartDateTime(lesson, stateDay.getDayOfWeek());
                Thread.sleep(ChronoUnit.MILLIS.between(now, startLessonDateTime) - TEN_MINUTES_IN_MILLIS);
                LOG.info("send notification to all students");
                sendAll(lesson.getName() + " in 10 minutes");
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                LOG.info("new schedule set");
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    private void checkState() {
        if (stateDay == null || stateDay.getLessons().size() <= stateIndex) {
            DayAndIndex dayAndIndex = scheduleService.getCurrentDayWithIndex();
            stateDay = dayAndIndex.getDay();
            stateIndex = dayAndIndex.getIndex();
        }
    }

    private void sendAll(String message) {
        subscribers.forEach(x -> {
            try {
                bot.sendMessage(message, x);
            } catch (TelegramApiException e) {
                LOG.error(e);
            }
        });
    }
}
