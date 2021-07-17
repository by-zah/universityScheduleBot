package ua.khnu.service;

import ua.khnu.entity.Group;

import java.util.List;

public interface GroupService {
    void createNewGroup(long userId, String message);

    List<Group> getAllGroups();

    List<Group> getUserGroups(long userId);
}
