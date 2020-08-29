package ua.khnu.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ua.khnu.entity.Group;
import ua.khnu.entity.User;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class GroupServiceTest {
    private static final long CHAT_ID = 1;
    private static final String GROUP_NAME = "qwerty";
    private static final String MESSAGE = "/createNewGroup";


    @Test
    void onCreateNewGroupShouldThrowExceptionIfMessageNotHaveGroupName() {
        GroupService groupService = new GroupService(null, null);

        Assertions.assertThrows(BotException.class, () -> groupService.createNewGroup(CHAT_ID, MESSAGE));
    }

    @Test
    void onCreateNewGroupShouldThrowExceptionIfGroupWithSpecifiedNameAlreadyExist() {
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.of(new Group()));
        GroupService groupService = new GroupService(groupRepository, null);

        Assertions.assertThrows(BotException.class, () -> groupService.createNewGroup(CHAT_ID, contextMessage));
    }

    @Test
    void shouldCallCreateOrUpdateOnGroupRepository() {
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.empty());
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getById(CHAT_ID)).thenReturn(Optional.of(new User()));
        GroupService groupService = new GroupService(groupRepository, userRepository);

        groupService.createNewGroup(CHAT_ID,contextMessage);

        verify(groupRepository).createOrUpdate(any());
    }

    @Test
    void shouldCallCreateOrUpdateOnUserRepositoryIfUserNotExist(){
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.empty());
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getById(CHAT_ID)).thenReturn(Optional.empty());
        GroupService groupService = new GroupService(groupRepository, userRepository);

        groupService.createNewGroup(CHAT_ID,contextMessage);

        verify(userRepository).createOrUpdate(any());
    }
    @Test
    void shouldNotCallCreateOrUpdateOnUserRepositoryIfUserExist(){
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.empty());
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getById(CHAT_ID)).thenReturn(Optional.of(new User()));
        GroupService groupService = new GroupService(groupRepository, userRepository);

        groupService.createNewGroup(CHAT_ID,contextMessage);

        verify(userRepository,never()).createOrUpdate(any());
    }
}
