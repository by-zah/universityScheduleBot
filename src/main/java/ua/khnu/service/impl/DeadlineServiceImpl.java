package ua.khnu.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.entity.Deadline;
import ua.khnu.entity.UserDeadline;
import ua.khnu.entity.pk.UserDeadlinePK;
import ua.khnu.exception.BotException;
import ua.khnu.repository.*;
import ua.khnu.service.DeadlineService;
import ua.khnu.util.csv.Csv;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class DeadlineServiceImpl implements DeadlineService {
    private static final Logger LOG = LogManager.getLogger(DeadlineServiceImpl.class);
    private static final long TIME_30_DAYS_IN_MILLIS = 2_592_000_000L;
    private static final long TIME_7_DAYS_IN_MILLIS = 604_800_000L;
    private static final long TIME_5_DAYS_IN_MILLIS = 432_000_000L;
    private static final long TIME_4_DAYS_IN_MILLIS = 345_600_000L;
    private static final long TIME_3_DAYS_IN_MILLIS = 259_200_000L;
    private static final long TIME_2_DAYS_IN_MILLIS = 172_800_000L;
    private static final long TIME_1_DAYS_IN_MILLIS = 86_400_000L;

    private final DeadlineRepository deadlineRepository;
    private final GroupRepository groupRepository;
    private final Csv csv;
    private final PeriodRepository periodRepository;
    private final UserRepository userRepository;
    private final UserDeadlineRepository userDeadlineRepository;
    private DeadlineSendMessageDemon deadlineSendMessageDemon;

    @Autowired
    public DeadlineServiceImpl(DeadlineRepository deadlineRepository, GroupRepository groupRepository, Csv csv, PeriodRepository periodRepository, UserRepository userRepository, UserDeadlineRepository userDeadlineRepository) {
        this.deadlineRepository = deadlineRepository;
        this.groupRepository = groupRepository;
        this.csv = csv;
        this.periodRepository = periodRepository;
        this.userRepository = userRepository;
        this.userDeadlineRepository = userDeadlineRepository;
    }

    public void setDeadlineSendMessageDemon(DeadlineSendMessageDemon deadlineSendMessageDemon) {
        this.deadlineSendMessageDemon = deadlineSendMessageDemon;
    }

    @Override
    @Transactional
    public void createDeadline(String groupName, String className, LocalDateTime localDateTime, String description) {
        createDeadline(Deadline.builder()
                .groupName(groupName)
                .className(className)
                .deadLineTime(localDateTime)
                .taskDescription(description)
                .build());
    }

    @Override
    @Transactional
    public void createDeadline(Deadline deadline) {
        validateDeadline(deadline.getGroupName(), deadline.getClassName(), deadline.getDeadLineTime());
        deadlineRepository.findByGroupNameAndClassNameAndDeadLineTime(deadline.getGroupName(), deadline.getClassName(), deadline.getDeadLineTime())
                .ifPresentOrElse(existDeadline -> {
                    var description = deadline.getTaskDescription();
                    LOG.info("Update existing deadline {}, with new description {}", existDeadline, description);
                    var newDescription = existDeadline.getTaskDescription() + " | " + description;
                    existDeadline.setTaskDescription(newDescription);
                    deadlineRepository.save(existDeadline);
                }, () -> {
                    LOG.info("Create new deadline {}", deadline);
                    deadlineRepository.save(deadline);
                    userRepository.findAllByGroupName(deadline.getGroupName()).stream()
                            .map(user -> new UserDeadline(new UserDeadlinePK(user.getId(), deadline.getId()), false))
                            .forEach(userDeadlineRepository::save);
                });
        deadlineSendMessageDemon.onNewDeadlineAdded();

    }

    @Override
    @Transactional
    public String getAllDeadlinesCsv() {
        final var allDeadlines = deadlineRepository.findAll();
        allDeadlines.sort(Comparator.comparing(Deadline::getDeadLineTime));
        return csv.createCsvFromObject(allDeadlines, Deadline.class);
    }

    @Override
    @Transactional
    public Deadline getNearestDeadline() {
        var deadlines = deadlineRepository.findByDeadLineTimeGreaterThan(LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)));
        if (deadlines.isEmpty()) {
            return null;
        }
        deadlines.sort(Comparator.comparing(Deadline::getDeadLineTime));
        final var deadline = deadlines.get(0);
        Hibernate.initialize(deadline.getRelatedGroup());
        return deadline;
    }

    @Override
    @Transactional
    public List<DeadlineNotificationDto> getNextDeadlineToNotification() {
        final var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        var deadlines = deadlineRepository.findByDeadLineTimeGreaterThan(now);
        var deadlinesInFuture = Stream.of(
                deadlinesToDto(deadlines, TIME_30_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_7_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_5_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_4_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_3_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_2_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_1_DAYS_IN_MILLIS)
        ).flatMap(deadlineNotificationDto -> deadlineNotificationDto)
                .filter(deadlineNotificationDto -> deadlineNotificationDto.isInFuture(now))
                .collect(Collectors.toList());
        var min = deadlinesInFuture.stream().min((d, d1) -> d.compareTo(d1, now));
        if (min.isEmpty()) {
            return List.of();
        }
        final var collect = deadlinesInFuture.stream()
                .filter(deadlineNotificationDto -> min.get().getMillisToNotification(now) == deadlineNotificationDto.getMillisToNotification(now))
                .collect(Collectors.toList());
        LOG.info("Next deadlines to notification:{}", collect);
        return collect;
    }

    @Override
    @Transactional
    public void markUserDeadlineAsDone(UserDeadlinePK userDeadlineId) {
        var userDeadline = userDeadlineRepository.findById(userDeadlineId);
        if (userDeadline.isEmpty()) {
            LOG.error("User deadline doesn't exist {}", userDeadlineId);
            throw new BotException("Deadline doesn't exist");
        }
        userDeadline.ifPresent(userDeadlineP -> {
            if (userDeadlineP.isDone()) {
                throw new BotException("Deadline already marks as done");
            }
            userDeadlineP.setDone(true);
        });
    }

    @Override
    @Transactional
    public boolean changeDeadlineDoneStatus(UserDeadlinePK userDeadlineId) {
        var userDeadline = userDeadlineRepository.findById(userDeadlineId);
        if (userDeadline.isEmpty()) {
            LOG.error("User deadline doesn't exist {}", userDeadlineId);
            throw new BotException("Deadline doesn't exist");
        }
        userDeadline.ifPresent(userDeadlineP -> userDeadlineP.setDone(!userDeadlineP.isDone()));
        return userDeadline.get().isDone();
    }

    @Override
    public List<Deadline> getDeadlinesUserCreate(int userId) {
        return deadlineRepository.findAllByCreatedById(userId);
    }

    @Override
    public void removeDeadline(int deadlineId) {
        if (!deadlineRepository.existsById(deadlineId)) {
            throw new BotException("The deadline doesn't exist");
        }
        deadlineRepository.deleteById(deadlineId);
    }

    private void validateDeadline(String groupName, String className, LocalDateTime localDateTime) {
        if (!groupRepository.existsById(groupName)) {
            throw new BotException("Group with following name doesn't exist");
        }
        var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        if (ChronoUnit.MILLIS.between(now, localDateTime) < 0) {
            throw new BotException("Can't add deadline in a past");
        }
        if (!periodRepository.existsByName(className)) {
            throw new BotException("Class with following name doesn't exist, recheck please");
        }
    }

    private Stream<DeadlineNotificationDto> deadlinesToDto(List<Deadline> deadlines, long millis) {
        return deadlines.stream()
                .map(deadline -> new DeadlineNotificationDto(deadline, millis));
    }
}
