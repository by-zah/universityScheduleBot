package ua.khnu.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.khnu.entity.Group;
import ua.khnu.entity.Period;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.PeriodRepository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PeriodServiceTest {
    static final String GROUP_NAME = "qwerty";
    public static final int USER_CHAT_ID = 123;
    List<Period> classes;
    List<Group> userGroups;

    @BeforeEach
    void setUp() {
        classes = new ArrayList<>();
        Period period = new Period();
        period.setIndex(1);
        period.setName("aaa");
        period.setRoomNumber("12");
        period.setGroupName(GROUP_NAME);
        period.setBuilding("qqf");
        period.setDay(1);
        classes.add(period);

        period = new Period();
        period.setIndex(2);
        period.setName("bbb");
        period.setRoomNumber("23");
        period.setGroupName(GROUP_NAME);
        period.setBuilding("zxc");
        period.setDay(2);
        classes.add(period);

        userGroups = new ArrayList<>();
        Group group = new Group();
        group.setName(GROUP_NAME);
        group.setOwnerId(USER_CHAT_ID);
        userGroups.add(group);
    }

    @Test
    void shouldThrowExceptionIfJsonIsInvalid() {
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenThrow(IllegalStateException.class);
        PeriodService periodService = new PeriodService(null, gson, null);

        Assertions.assertThrows(BotException.class, () -> periodService.addAllFromJson("aa", 1));
    }

    @Test
    void shouldThrowExceptionIfUserTryAddClassesForGroupThatHeDoesNotOwn() {
        Period period = new Period();
        period.setGroupName("aaaa");
        classes.add(period);
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(classes);
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getAllUserGroups(USER_CHAT_ID)).thenReturn(userGroups);
        PeriodService periodService = new PeriodService(null, gson, groupRepository);

        Assertions.assertThrows(BotException.class, () -> periodService.addAllFromJson("", USER_CHAT_ID));
    }

    @Test
    void shouldCallCreateAllOnPeriodRepository(){
        Gson gson = mock(Gson.class);
        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(classes);
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getAllUserGroups(USER_CHAT_ID)).thenReturn(userGroups);
        PeriodRepository periodRepository = mock(PeriodRepository.class);
        PeriodService periodService = new PeriodService(periodRepository, gson, groupRepository);

        periodService.addAllFromJson("",USER_CHAT_ID);

        verify(periodRepository).createAll(anyList());
    }
}
