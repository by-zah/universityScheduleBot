package ua.khnu.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.PeriodRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PeriodService {
    public static final Type PERIOD_LIST_TYPE = TypeToken.getParameterized(List.class, Period.class).getType();
    private final PeriodRepository periodRepository;
    private final GroupRepository groupRepository;
    private final Gson gson;

    @Autowired
    public PeriodService(PeriodRepository periodRepository, Gson gson, GroupRepository groupRepository) {
        this.periodRepository = periodRepository;
        this.gson = gson;
        this.groupRepository = groupRepository;
    }

    public void addAllFromJson(String json, long userChatId) {
        try {
            List<Period> classes = gson.fromJson(json, PERIOD_LIST_TYPE);
            List<String> groupNames = groupRepository.getAllUserGroups(userChatId).stream()
                    .map(Group::getName)
                    .collect(Collectors.toList());
            boolean containsOnlyClassesForGroupThatUserOwn = classes.stream()
                    .map(Period::getGroupName)
                    .allMatch(groupNames::contains);
            if (!containsOnlyClassesForGroupThatUserOwn) {
                throw new BotException("Error, the file contains classes for a group that you don't own");
            }
            periodRepository.createAll(classes);
        }catch (IllegalStateException e){
            throw new BotException("Invalid json");
        }
    }

}
