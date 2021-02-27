package ua.khnu.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.khnu.Bot;
import ua.khnu.commands.MarkDeadlineAsDoneCommand;
import ua.khnu.dto.ScheduleContainer;
import ua.khnu.entity.Deadline;
import ua.khnu.entity.UserDeadline;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.MailingService;
import ua.khnu.service.PeriodService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
import static ua.khnu.util.Constants.TIME_ZONE_ID;
import static ua.khnu.util.KeyboardBuilder.buildInlineKeyboard;
import static ua.khnu.util.MessageSender.execute;

@Service
public class MailingServiceImpl implements MailingService {
    private static final Logger LOG = LogManager.getLogger(MailingServiceImpl.class);

    private final ScheduleContainer scheduleContainer;
    private final ExecutorService executorService;
    private final Queue<SendMessage> queue;
    private final AtomicBoolean isMailingRunning;
    private final PeriodService periodService;
    private final UserRepository userRepository;
    private Bot bot;

    @Autowired
    public MailingServiceImpl(ScheduleContainer scheduleContainer, PeriodService periodService, UserRepository userRepository) {
        this.scheduleContainer = scheduleContainer;
        this.periodService = periodService;
        this.userRepository = userRepository;
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
        LOG.info("Perform planned mailing periodIndex {}, dayOfWeek {}", periodIndex, dayOfWeek);
        periodService.getPeriodByDayAndIndex(periodIndex, dayOfWeek).forEach(period -> {
            var students = period.getGroup().getStudents().stream()
                    .filter(student -> student.getSettings().isClassNotificationsEnabled())
                    .collect(Collectors.toList());
            String message = period.getName() + " in 10 minutes in room " + period.getRoomNumber();
            message = period.getBuilding() == null ?
                    message : message + " in " + period.getBuilding() + " building";
            String finalMessage = message;
            sendMailingMessages(students.stream()
                    .map(user -> {
                        var sendMessage = new SendMessage();
                        sendMessage.setChatId(String.valueOf(user.getChatId()));
                        sendMessage.setText(finalMessage);
                        return sendMessage;
                    })
                    .collect(Collectors.toList()));
        });
    }

    @Override
    public void sendMailingMessages(List<SendMessage> messages) {
        queue.addAll(messages);
        LOG.info("{} messages added to queue, current queue size is {}", messages.size(), queue.size());
        if (!isMailingRunning.get()) {
            performMailing();
        }
    }

    @Override
    @Transactional
    public void sendDeadlineNotifications(List<Deadline> deadlines) {
        deadlines.forEach(this::sendDeadlineNotification);
    }

    private void sendDeadlineNotification(Deadline deadline) {
        Hibernate.initialize(deadline.getUserDeadlines());
        LOG.info("Send notifications for deadline {}", deadline);
        final var now = LocalDate.now(ZoneId.of(TIME_ZONE_ID));
        final var userDeadlines = deadline.getUserDeadlines();
        var ids = userRepository.findAllByIdInAndSettingsIsDeadlineNotificationsEnabled(
                userDeadlines.stream()
                        .map(userDeadline -> userDeadline.getId().getUserId())
                        .collect(Collectors.toList())
                ,true);
        var messages = userDeadlines
                .stream()
                .filter(userDeadline -> ids.contains(userDeadline.getId().getUserId()))
                .map(userDeadline -> {
                    var timePeriod = Period.between(now, deadline.getDeadLineTime().toLocalDate());
                    var message = timePeriod.getDays() + " days left to deadline by discipline \"" + deadline.getClassName() + "\" here is task description:\n" + deadline.getTaskDescription();
                    var sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(buildInlineKeyboard("/" + MarkDeadlineAsDoneCommand.COMMAND_IDENTIFIER, List.of(String.valueOf(deadline.getId())), List.of("Mark as done")));
                    sendMessage.setChatId(String.valueOf(userDeadline.getId().getUserId()));
                    sendMessage.setText(message);
                    return sendMessage;
                }).collect(Collectors.toList());
        sendMailingMessages(messages);
    }

    private void performMailing() {
        executorService.execute(() -> {
            LOG.info("mailing thread is started");
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
                        execute(bot, m);
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
        LOG.info("Mailing thread is going to sleep, queue size = {}", queue.size());
        Thread.sleep(ONE_SECOND_IN_MILLIS);
        countTimes.clear();
    }

    private Optional<SendMessage> queuePoll() {
        return Optional.ofNullable(queue.poll());
    }
}
