package ua.khnu.service;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.dto.File;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.entity.PeriodType;
import ua.khnu.exception.BotException;
import ua.khnu.exception.CsvException;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.repository.UserRepository;
import ua.khnu.util.Csv;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ua.khnu.entity.PeriodType.REGULAR;
import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class PeriodService {
    public static final Type PERIOD_LIST_TYPE = TypeToken.getParameterized(List.class, Period.class).getType();
    private static final int DAYS_IN_WEEK = 7;
    private final PeriodRepository periodRepository;
    private final Gson gson;
    private final UserRepository userRepository;
    private final Map<String, Function<String, List<Period>>> parsers;
    private final Csv csvParser;

    @Autowired
    public PeriodService(PeriodRepository periodRepository, Gson gson, UserRepository userRepository, Csv csvParser) {
        this.periodRepository = periodRepository;
        this.gson = gson;
        this.userRepository = userRepository;
        this.csvParser = csvParser;
        this.parsers = ImmutableMap.<String, Function<String, List<Period>>>builder()
                .put("json", this::parseJson)
                .put("csv", this::parseCsv)
                .build();
    }

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

    public List<Period> parseJson(String json) {
        return gson.fromJson(json, PERIOD_LIST_TYPE);
    }

    public List<Period> parseCsv(String csv) {
        try {
            return csvParser.read(csv, Period.class);
        } catch (CsvException e) {
            throw new BotException("Invalid file");
        }
    }

    @Transactional
    public List<Period> getPeriodByDayAndIndex(int periodIndex, DayOfWeek dayOfWeek) {
        return periodRepository.findAllByIdIndexAndIdDayAndIdPeriodTypeIn(periodIndex, dayOfWeek, List.of(getEvenOrOdd(), REGULAR));
    }

    public void removeAll(List<Period> periods) {
        periodRepository.deleteAll(periods);
    }

    public PeriodType getEvenOrOdd() {
        return new GregorianCalendar().get(Calendar.WEEK_OF_YEAR) % 2 == 0 ? PeriodType.EVEN_WEEKS : PeriodType.ODD_WEEKS;
    }

}
