package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Group;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void createNewGroup(int userId, String message) {
        String[] args = message.split(" ");
        if (args.length != 2) {
            throw new BotException("this command allows only 1 argument - group name");
        }
        String groupName = args[1];
        Optional<Group> groupOpt = groupRepository.getByGroupName(groupName);
        if (groupOpt.isPresent()) {
            throw new BotException("Group with this name already exist");
        }
        Group group = new Group();
        group.setName(groupName);
        group.setOwnerId(userId);
        groupRepository.createOrUpdate(group);
    }

    public List<Group> getAllGroups() {
        return groupRepository.getAll();
    }
}
