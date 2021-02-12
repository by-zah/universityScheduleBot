package ua.khnu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.Bot;
import ua.khnu.dto.MessageForQueue;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.service.MailingService;
import ua.khnu.service.PeriodService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ua.khnu.util.Constants.ONE_SECOND_IN_MILLIS;
import static ua.khnu.util.MessageSender.sendMessage;

@Service
public class MailingServiceImpl implements MailingService {

    private final ScheduleContainer scheduleContainer;
    private final ExecutorService executorService;
    private final Queue<MessageForQueue> queue;
    private final AtomicBoolean isMailingRunning;
    private final PeriodService periodService;
    private Bot bot;

    @Autowired
    public MailingServiceImpl(ScheduleContainer scheduleContainer, PeriodService periodService) {
        this.scheduleContainer = scheduleContainer;
        this.periodService = periodService;
        executorService = Executors.newSingleThreadExecutor();
        queue = new ConcurrentLinkedQueue<>();
        isMailingRunning = new AtomicBoolean(false);
    }

    //to resolve circulation dependencies
    @Override
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public boolean isReady() {
        return !scheduleContainer.getSchedule().isEmpty();
    }

    @Override
    @Transactional
    public void sendClassNotifications(int periodIndex, DayOfWeek dayOfWeek) {
        periodService.getPeriodByDayAndIndex(periodIndex, dayOfWeek).forEach(period -> {
            var students = period.getGroup().getStudents();
            String message = period.getName() + " in 10 minutes in room " + period.getRoomNumber();
            message = period.getBuilding() == null ?
                    message : message + " in " + period.getBuilding() + " building";
            String finalMessage = message;
            sendMailingMessages(students.stream()
                    .map(user -> new MessageForQueue(finalMessage, user.getChatId()))
                    .collect(Collectors.toList()));
        });
    }

    @Override
    public void sendMailingMessages(List<MessageForQueue> messages) {
        queue.addAll(messages);
        if (!isMailingRunning.get()) {
            performMailing();
        }
    }

    private void performMailing() {
        executorService.execute(() -> {
            try {
                List<Long> countTimes = new ArrayList<>();
                isMailingRunning.set(true);
                while (true) {
                    var message = queuePoll();
                    if (message.isEmpty() && queue.isEmpty()) {
                        wait(countTimes);
                        if (queue.isEmpty()) {
                            break;
                        }
                        message = queuePoll();
                    }
                    if (message.isPresent()) {
                        var m = message.get();
                        if (countTimes.size() == 30) {
                            countTimes.removeIf(l -> System.currentTimeMillis() - l >= ONE_SECOND_IN_MILLIS);
                        }
                        if (countTimes.size() == 30) {
                            wait(countTimes);
                        }
                        sendMessage(bot, m.getChatId(), m.getMessage());
                        countTimes.add(System.currentTimeMillis());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isMailingRunning.set(false);
            }
        });
    }

    private void wait(List<Long> countTimes) throws InterruptedException {
        Thread.sleep(ONE_SECOND_IN_MILLIS);
        countTimes.clear();
    }

    private Optional<MessageForQueue> queuePoll() {
        return Optional.ofNullable(queue.poll());
    }
}
