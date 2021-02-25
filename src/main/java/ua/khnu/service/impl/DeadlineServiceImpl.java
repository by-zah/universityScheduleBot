package ua.khnu.service.impl;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.entity.Deadline;
import ua.khnu.exception.BotException;
import ua.khnu.repository.DeadlineRepository;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.service.DeadlineService;
import ua.khnu.util.csv.Csv;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class DeadlineServiceImpl implements DeadlineService {
    private static final long TIME_30_DAYS_IN_MILLIS = 2_592_000_000L;
    private static final long TIME_7_DAYS_IN_MILLIS = 604_800_000L;
    private static final long TIME_5_DAYS_IN_MILLIS = 432_000_000L;
    private static final long TIME_1_DAYS_IN_MILLIS = 86_400_000L;

    private final DeadlineRepository deadlineRepository;
    private final GroupRepository groupRepository;
    private final Csv csv;
    private final PeriodRepository periodRepository;
    private DeadlineSendMessageDemon deadlineSendMessageDemon;

    @Autowired
    public DeadlineServiceImpl(DeadlineRepository deadlineRepository, GroupRepository groupRepository, Csv csv, PeriodRepository periodRepository) {
        this.deadlineRepository = deadlineRepository;
        this.groupRepository = groupRepository;
        this.csv = csv;
        this.periodRepository = periodRepository;
    }

    public void setDeadlineSendMessageDemon(DeadlineSendMessageDemon deadlineSendMessageDemon) {
        this.deadlineSendMessageDemon = deadlineSendMessageDemon;
    }

    @Override
    @Transactional
    public void createDeadline(String groupName, String className, LocalDateTime localDateTime, String description) {
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
        deadlineRepository.findByGroupNameAndClassNameAndDeadLineTime(groupName, className, localDateTime)
                .ifPresentOrElse(deadline -> {
                    var newDescription = deadline.getTaskDescription() + " | " + description;
                    deadline.setTaskDescription(newDescription);
                    deadlineRepository.save(deadline);
                }, () -> {
                    var deadLine = Deadline.builder()
                            .deadLineTime(localDateTime)
                            .groupName(groupName)
                            .taskDescription(description)
                            .className(className)
                            .build();
                    deadlineRepository.save(deadLine);
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
        Hibernate.initialize(deadline.getRelatedGroups());
        return deadline;
    }

    @Override
    @Transactional
    public Optional<DeadlineNotificationDto> getNextDeadlineToNotification() {
        var deadlines = deadlineRepository.findByDeadLineTimeGreaterThan(LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)));
        var res = Stream.of(
                deadlinesToDto(deadlines, TIME_30_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_7_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_5_DAYS_IN_MILLIS),
                deadlinesToDto(deadlines, TIME_1_DAYS_IN_MILLIS)
        ).flatMap(deadlineNotificationDto -> deadlineNotificationDto)
                .filter(DeadlineNotificationDto::isInFuture)
                .min(DeadlineNotificationDto::compareTo);
        res.ifPresent(deadline -> Hibernate.initialize(deadline.getDeadline().getRelatedGroups()));
        return res;
    }

    private Stream<DeadlineNotificationDto> deadlinesToDto(List<Deadline> deadlines, long millis) {
        return deadlines.stream()
                .map(deadline -> new DeadlineNotificationDto(deadline, millis));
    }
}
