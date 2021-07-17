package ua.khnu.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.SubscriptionRepository;
import ua.khnu.service.impl.SubscriptionServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class SubscriptionServiceTest {
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private GroupRepository groupRepository;

    private long chatId;
    private String groupName;

    @BeforeClass
    private void beforeClass() {
        openMocks(this);
        chatId = 1L;
        groupName = "test";
    }

    @BeforeMethod
    public void setUp() {
        reset(subscriptionRepository, groupRepository);
        when(groupRepository.existsById(groupName)).thenReturn(true);
        when(subscriptionRepository.existsById(any())).thenReturn(false);
    }

    @Test(expectedExceptions = BotException.class)
    public void testSubscribeWhenGroupDoesntExist() {
        when(groupRepository.existsById(groupName)).thenReturn(false);

        subscriptionService.subscribe(chatId, groupName);
    }

    @Test(expectedExceptions = BotException.class)
    public void testSubscribeWhenSubscriptionAlreadyExist() {
        when(subscriptionRepository.existsById(any())).thenReturn(true);

        subscriptionService.subscribe(chatId, groupName);
    }

    @Test
    public void testSubscribe() {
        subscriptionService.subscribe(chatId, groupName);

        verify(subscriptionRepository).save(any());
    }
}
