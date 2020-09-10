package ua.khnu.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.khnu.entity.Group;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class GroupServiceTest {
    private static final int USER_ID = 1;
    private static final String GROUP_NAME = "qwerty";
    private static final String MESSAGE = "/createNewGroup";

    @Test
    void onCreateNewGroupShouldThrowExceptionIfMessageNotHaveGroupName() {
        GroupService groupService = new GroupService(null);

        Assertions.assertThrows(BotException.class, () -> groupService.createNewGroup(USER_ID, MESSAGE));
    }

    @Test
    void onCreateNewGroupShouldThrowExceptionIfGroupWithSpecifiedNameAlreadyExist() {
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.of(new Group()));
        GroupService groupService = new GroupService(groupRepository);

        Assertions.assertThrows(BotException.class, () -> groupService.createNewGroup(USER_ID, contextMessage));
    }

    @Test
    void shouldCallCreateOrUpdateOnGroupRepository() {
        String contextMessage = MESSAGE + " " + GROUP_NAME;
        GroupRepository groupRepository = mock(GroupRepository.class);
        when(groupRepository.getByGroupName(GROUP_NAME)).thenReturn(Optional.empty());
        GroupService groupService = new GroupService(groupRepository);

        groupService.createNewGroup(USER_ID, contextMessage);

        verify(groupRepository).createOrUpdate(any());
    }

    @Test
    void shouldReturnAllGroupsFromRepository(){
        GroupRepository groupRepository = mock(GroupRepository.class);
        ArrayList<Group> expected = new ArrayList<>();
        expected.add(new Group());
        when(groupRepository.getAll()).thenReturn(expected);
        GroupService groupService = new GroupService(groupRepository);

        List<Group> actual = groupService.getAllGroups();

        verify(groupRepository).getAll();
        Assertions.assertEquals(expected,actual);
    }
}
