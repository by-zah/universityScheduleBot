package ua.khnu.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.khnu.demon.DeadlineSendMessageDemon;
import ua.khnu.entity.Deadline;
import ua.khnu.exception.BotException;
import ua.khnu.repository.DeadlineRepository;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.PeriodRepository;
import ua.khnu.service.impl.DeadlineServiceImpl;
import ua.khnu.util.csv.Csv;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
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

    @BeforeClass
    public void init() {
        initMocks(this);
        deadlineService.setDeadlineSendMessageDemon(mock(DeadlineSendMessageDemon.class));
    }

    @BeforeMethod
    public void beforeEach() {
        reset(deadlineRepository, groupRepository, csv, periodRepository);
        when(groupRepository.existsById(anyString())).thenReturn(true);
        when(periodRepository.existsByName(anyString())).thenReturn(true);
    }

    @Test(expectedExceptions = BotException.class)
    public void testCreateDeadlineWhenDateIsInPastThrowException() {
        var localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay().minusDays(1);

        deadlineService.createDeadline("", "", localDateTime, "");
    }

    @Test(expectedExceptions = BotException.class)
    public void testCreateDeadlineWhenGroupWithNameDontExist() {
        var groupName = "group";
        var localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay().plusDays(1);
        when(groupRepository.existsById(groupName)).thenReturn(false);

        deadlineService.createDeadline(groupName, "", localDateTime, "");
    }

    @Test
    public void testCreateDeadlineHappyPath() {
        var groupName = "group";
        var localDateTime = LocalDate.now(ZoneId.of(TIME_ZONE_ID)).atStartOfDay().plusDays(1);

        deadlineService.createDeadline(groupName, "", localDateTime, "");

        verify(deadlineRepository).save(any(Deadline.class));
    }
}