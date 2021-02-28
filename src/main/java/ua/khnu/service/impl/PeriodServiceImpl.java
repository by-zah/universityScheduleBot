package ua.khnu.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.dto.File;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.entity.PeriodType;
import ua.khnu.entity.pk.PeriodPK;
import ua.khnu.exception.BotException;
import ua.khnu.exception.CsvException;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.PeriodService;
import ua.khnu.util.csv.Csv;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.khnu.entity.PeriodType.REGULAR;
import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class PeriodServiceImpl implements PeriodService {
    public static final Type PERIOD_LIST_TYPE = TypeToken.getParameterized(List.class, Period.class).getType();
    private static final int DAYS_IN_WEEK = 7;
    private static final String THERE_ARENT_ANY_CLASS_YOU_ARE_ABLE_TO_DELETE = "There aren't any class you are able to delete";
    private final PeriodRepository periodRepository;
    private final Gson gson;
    private final UserRepository userRepository;
    private final Map<String, Function<String, List<Period>>> parsers;
    private final Csv csvParser;

    @Autowired
    public PeriodServiceImpl(PeriodRepository periodRepository, Gson gson, UserRepository userRepository, Csv csvParser) {
        this.periodRepository = periodRepository;
        this.gson = gson;
        this.userRepository = userRepository;
        this.csvParser = csvParser;
        this.parsers = ImmutableMap.<String, Function<String, List<Period>>>builder()
                .put("json", this::parseJson)
                .put("csv", this::parseCsv)
                .build();
    }

    @Override
    @Transactional
    public List<Period> getUpcomingUserClasses(int userId) {
        var groups = userRepository.findById(userId)
                .orElseThrow(() -> new BotException("You aren't registered"))
                .getGroups();
        if (groups.isEmpty()) {
            throw new BotException("You don't subscribe to any group, use /subscribe to choose one or more");
        }
        var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID));
        var calls = 0;
        List<Period> classes;
        do {
            final var finalNow = now;
            classes = periodRepository.findByGroupInAndIdDay(groups, now.getDayOfWeek())
                    .stream()
                    .filter(period -> REGULAR.equals(period.getPeriodType()) || getEvenOrOdd().equals(period.getPeriodType()))
                    .filter(period -> ChronoUnit.MILLIS.between(finalNow, period.getScheduleUnit().getStartLocalDateTime(finalNow)) > 0)
                    .collect(Collectors.toList());
            now = now.toLocalDate().atStartOfDay().plusDays(1);
            calls++;
            if (calls >= DAYS_IN_WEEK) {
                throw new BotException("There aren't any classes yet");
            }
        } while (classes.isEmpty());
        return classes;
    }

    @Override
    @Transactional
    public void addAll(File file, int userId) {
        try {
            var user = userRepository.findById(userId);
            if (user.isEmpty()) {
                throw new BotException("You aren't registered");
            }
            Function<String, List<Period>> parser = parsers.get(file.getExtension());
            if (parser == null) {
                throw new BotException("Unsupported file extension");
            }
            List<Period> classes = parser.apply(file.getContent());
            var groupNames = user.get().getGroups().stream()
                    .map(Group::getName)
                    .collect(Collectors.toList());
            boolean containsOnlyClassesForGroupThatUserOwn = classes.stream()
                    .map(Period::getGroupName)
                    .allMatch(groupNames::contains);
            if (!containsOnlyClassesForGroupThatUserOwn) {
                throw new BotException("Error, the file contains classes for a group that you don't own");
            }
            periodRepository.saveAll(classes);
        } catch (IllegalStateException e) {
            throw new BotException("Invalid file");
        }

    }

    @Override
    @Transactional
    public List<Period> getPeriodByDayAndIndex(int periodIndex, DayOfWeek dayOfWeek) {
        return periodRepository.findAllByIdIndexAndIdDayAndIdPeriodTypeIn(periodIndex, dayOfWeek, List.of(getEvenOrOdd(), REGULAR));
    }

    @Override
    @Transactional
    public void removeAllClassesInGroupsUserOwn(int userId) {
        userRepository.findById(userId).ifPresentOrElse(user -> {
                    var periods = user.getGroupsUserOwn().stream()
                            .map(Group::getPeriods)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    if (periods.isEmpty()) {
                        throw new BotException(THERE_ARENT_ANY_CLASS_YOU_ARE_ABLE_TO_DELETE);
                    }
                    periodRepository.deleteAll(periods);
                }
                , () -> {
                    throw new BotException(THERE_ARENT_ANY_CLASS_YOU_ARE_ABLE_TO_DELETE);
                });
    }

    @Override
    @Transactional
    public void updateClassRoom(PeriodPK id, String newRoom) {
        var period = periodRepository.findById(id)
                .orElseThrow(() -> new BotException("Period doesn't exist"));
        period.setRoomNumber(newRoom);
        periodRepository.save(period);
    }

    @Override
    @Transactional
    public List<Period> getAllPeriods() {
        var periodList = periodRepository.findAll();
        periodList.forEach(Hibernate::initialize);
        return periodList;
    }

    @Override
    public List<String> getAllClassesNames() {
        return periodRepository.findAllDistinctByName();
    }

    private List<Period> parseJson(String json) {
        return gson.fromJson(json, PERIOD_LIST_TYPE);
    }

    private List<Period> parseCsv(String csv) {
        try {
            return csvParser.read(csv, Period.class);
        } catch (CsvException e) {
            throw new BotException("Invalid file");
        }
    }

    private PeriodType getEvenOrOdd() {
        return new GregorianCalendar().get(Calendar.WEEK_OF_YEAR) % 2 == 0 ? PeriodType.EVEN_WEEKS : PeriodType.ODD_WEEKS;
    }

}
