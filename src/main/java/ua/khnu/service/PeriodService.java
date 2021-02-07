package ua.khnu.service;

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

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PeriodService {
    public static final Type PERIOD_LIST_TYPE = TypeToken.getParameterized(List.class, Period.class).getType();
    private final PeriodRepository periodRepository;
    private final Gson gson;
    private final UserRepository userRepository;
    private final Map<String, Function<String, List<Period>>> parsers;
    private final Csv csvParser;

    @Autowired
    public PeriodService(PeriodRepository periodRepository, Gson gson, UserRepository userRepository) {
        this.periodRepository = periodRepository;
        this.gson = gson;
        this.userRepository = userRepository;
        this.parsers = new HashMap<>();
        csvParser = new Csv();
    }

    @PostConstruct
    private void initParsers() {
        parsers.put("json", this::parseJson);
        parsers.put("csv", this::parseCsv);
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
        return periodRepository.findAllByIdIndexAndIdDayAndIdPeriodTypeIn(periodIndex, dayOfWeek, List.of(getEvenOrOdd(), PeriodType.REGULAR));
    }

    public void removeAll(List<Period> periods) {
        periodRepository.deleteAll(periods);
    }

    private PeriodType getEvenOrOdd() {
        return new GregorianCalendar().get(Calendar.WEEK_OF_YEAR) % 2 == 0 ? PeriodType.EVEN_WEEKS : PeriodType.ODD_WEEKS;
    }

}
