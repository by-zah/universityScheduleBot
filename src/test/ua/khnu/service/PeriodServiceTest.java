package ua.khnu.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.khnu.entity.*;
import ua.khnu.exception.BotException;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.repository.UserRepository;
import ua.khnu.service.impl.PeriodServiceImpl;
import ua.khnu.util.Csv;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertThrows;
import static ua.khnu.util.Constants.TIME_ZONE_ID;


public class PeriodServiceTest {
    @InjectMocks
    private PeriodServiceImpl periodService;

    @Mock
    private PeriodRepository periodRepository;
    @Mock
    private Gson gson;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Csv csvParser;

    private final int userId = 1;

    @BeforeClass
    private void beforeClass() {
        initMocks(this);
    }

    @BeforeMethod
    private void beforeEach() {
        reset(periodRepository, gson, userRepository, csvParser);
    }

    @Test
    public void testGetUpcomingUserClassesWhenUserDoesntExist() {
        //given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //when, then
        assertThrows(BotException.class, () -> periodService.getUpcomingUserClasses(userId));
    }

    @Test
    public void testGetUpcomingUserClassesWhenUserDoesntSubscribeAnyGroup() {
        //given
        var user = mock(User.class);
        when(user.getGroups()).thenReturn(ImmutableList.of());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //when, then
        assertThrows(BotException.class, () -> periodService.getUpcomingUserClasses(userId));
        verify(user).getGroups();
    }

    @Test
    public void testGetUpcomingUserClassesWhenGroupsUserSubscribeDontHaveClasses() {
        //given
        var user = mock(User.class);
        var groups = ImmutableList.of(mock(Group.class));
        when(user.getGroups()).thenReturn(groups);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //when, then
        assertThrows(BotException.class, () -> periodService.getUpcomingUserClasses(userId));
        verify(user, times(1)).getGroups();
        verify(periodRepository, times(7)).findByGroupInAndIdDay(anyList(), any(DayOfWeek.class));
    }

    @Test
    public void testGetUpcomingUserClassesHappyPath() {
        //given
        var now = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).plusDays(1);
        var user = mock(User.class);
        var groups = ImmutableList.of(mock(Group.class));
        var period = mock(Period.class);
        var scheduleUnit = mock(ScheduleUnit.class);
        when(period.getScheduleUnit()).thenReturn(scheduleUnit);
        when(period.getPeriodType()).thenReturn(PeriodType.REGULAR);
        when(user.getGroups()).thenReturn(groups);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(scheduleUnit.getStartLocalDateTime(any(LocalDateTime.class))).thenReturn(now.plusHours(1));
        var expectedClasses = ImmutableList.of(period);
        when(periodRepository.findByGroupInAndIdDay(anyList(), eq(now.getDayOfWeek())))
                .thenReturn(expectedClasses);

        //when
        var classes = periodService.getUpcomingUserClasses(userId);

        //then
        Assert.assertEquals(classes, expectedClasses);
        verify(user, times(1)).getGroups();
        verify(periodRepository, times(2)).findByGroupInAndIdDay(anyList(), any(DayOfWeek.class));
    }
}
