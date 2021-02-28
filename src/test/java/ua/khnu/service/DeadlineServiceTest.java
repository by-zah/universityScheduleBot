package ua.khnu.service;

import org.hamcrest.collection.IsEmptyCollection;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.dto.DeadlineNotificationDto;
import ua.khnu.entity.Deadline;
import ua.khnu.entity.UserDeadline;
import ua.khnu.exception.BotException;
import ua.khnu.repository.*;
import ua.khnu.service.impl.DeadlineServiceImpl;
import ua.khnu.util.csv.Csv;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.testng.Assert.assertEquals;
import static ua.khnu.util.Constants.TIME_ZONE_ID;


public class DeadlineServiceTest {
    @InjectMocks
    private DeadlineServiceImpl deadlineService;

    @Mock
    private DeadlineRepository deadlineRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private Csv csv;

    @Mock
    private PeriodRepository periodRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDeadlineRepository userDeadlineRepository;

    private LocalDateTime localDateTime;

    @BeforeClass
    public void init() {
        openMocks(this);
        deadlineService.setDeadlineSendMessageDemon(mock(DeadlineSendMessageDemon.class));
        localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay().plusDays(1);
    }

    @BeforeMethod
    public void beforeEach() {
        reset(deadlineRepository, groupRepository, csv, periodRepository, userDeadlineRepository, userRepository);
        when(groupRepository.existsById(anyString())).thenReturn(true);
        when(periodRepository.existsByName(anyString())).thenReturn(true);
    }

    @Test(expectedExceptions = BotException.class)
    public void testCreateDeadlineWhenDateIsInPastThrowException() {
        var localDateTimeLocal = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay().minusDays(1);

        deadlineService.createDeadline("", "", localDateTimeLocal, "");
    }

    @Test(expectedExceptions = BotException.class)
    public void testCreateDeadlineWhenGroupWithNameDontExist() {
        when(groupRepository.existsById(anyString())).thenReturn(false);

        deadlineService.createDeadline("", "", localDateTime, "");
    }


    @Test(expectedExceptions = BotException.class)
    public void testCreateDeadlineWhenPeriodNameDontExist() {
        when(periodRepository.existsByName(anyString())).thenReturn(false);

        deadlineService.createDeadline("", "", localDateTime, "");
    }

    @Test
    public void testCreateDeadlineHappyPath() {
        deadlineService.createDeadline("", "", localDateTime, "");

        verify(deadlineRepository).save(any(Deadline.class));
    }

    @Test
    public void testCreateDeadlineWhenDeadlineExist() {
        var deadline = mock(Deadline.class);
        when(deadlineRepository.findByGroupNameAndClassNameAndDeadLineTime(anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(deadline));

        deadlineService.createDeadline("", "", localDateTime, "");

        verify(deadline).setTaskDescription(anyString());
    }

    @Test
    public void testGetAllDeadlinesCsv() {
        final var expected = "test";
        List<Deadline> deadlines = new ArrayList<>();
        deadlines.add(mock(Deadline.class));
        when(deadlineRepository.findAll()).thenReturn(deadlines);
        when(csv.createCsvFromObject(deadlines, Deadline.class)).thenReturn(expected);

        var actual = deadlineService.getAllDeadlinesCsv();

        assertEquals(expected, actual);
    }

    @Test(expectedExceptions = BotException.class)
    public void testMarkUserDeadlineAsDoneWhenDeadlineDontExist() {
        deadlineService.markUserDeadlineAsDone(null);
    }

    @Test(expectedExceptions = BotException.class)
    public void testMarkUserDeadlineAsDoneWhenDeadlineAlreadyMarked() {
        var userDeadline = mock(UserDeadline.class);
        when(userDeadline.isDone()).thenReturn(true);
        when(userDeadlineRepository.findById(any())).thenReturn(Optional.of(userDeadline));

        deadlineService.markUserDeadlineAsDone(null);
    }

    @Test
    public void testMarkUserDeadlineAsDoneWhenDeadlineNotDone() {
        var userDeadline = mock(UserDeadline.class);
        when(userDeadline.isDone()).thenReturn(false);
        when(userDeadlineRepository.findById(any())).thenReturn(Optional.of(userDeadline));

        deadlineService.markUserDeadlineAsDone(null);

        verify(userDeadline).setDone(true);
    }

    @Test
    public void testGetNextDeadlineToNotificationWhenThereIsNotDeadlinesInFuture() {
        var actual = deadlineService.getNextDeadlineToNotification();

        assertThat(actual, IsEmptyCollection.empty());
    }

    @Test
    public void testGetNextDeadlineToNotificationWhenThereIsThreeDeadlineToNotifyInOneTime() {
        final var value = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).plusDays(1).plusHours(1);
        var deadline1 = mock(Deadline.class);
        when(deadline1.getDeadLineTime()).thenReturn(value);
        var deadline2 = mock(Deadline.class);
        when(deadline2.getDeadLineTime()).thenReturn(value.plusDays(1));
        var deadline3 = mock(Deadline.class);
        when(deadline3.getDeadLineTime()).thenReturn(value.plusDays(2));
        when(deadlineRepository.findByDeadLineTimeGreaterThan(any(LocalDateTime.class)))
                .thenReturn(List.of(deadline1, deadline2, deadline3));

        var actual = deadlineService.getNextDeadlineToNotification()
                .stream()
                .map(DeadlineNotificationDto::getDeadline)
                .collect(Collectors.toList());

        assertThat(actual, containsInAnyOrder(deadline1, deadline2, deadline3));
    }

    @Test
    public void testGetNextDeadlineToNotificationWhenFewDeadlines() {
        final var value = LocalDateTime.now(ZoneId.of(TIME_ZONE_ID)).plusDays(2).plusHours(1);
        var deadline1 = mock(Deadline.class);
        when(deadline1.getDeadLineTime()).thenReturn(value);
        var deadline2 = mock(Deadline.class);
        when(deadline2.getDeadLineTime()).thenReturn(value.plusDays(1).minusHours(2));
        var deadline3 = mock(Deadline.class);
        when(deadline3.getDeadLineTime()).thenReturn(value.minusDays(1).plusHours(5));
        when(deadlineRepository.findByDeadLineTimeGreaterThan(any(LocalDateTime.class)))
                .thenReturn(List.of(deadline1, deadline2, deadline3));

        var actual = deadlineService.getNextDeadlineToNotification()
                .stream()
                .map(DeadlineNotificationDto::getDeadline)
                .collect(Collectors.toList());

        assertEquals(List.of(deadline1), actual);
    }
}