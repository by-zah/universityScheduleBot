package ua.khnu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Group;
import ua.khnu.entity.User;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.UserRepository;

import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public void createNewGroup(long ownerChatId, String message) {
        String[] args = message.split(" ");
        if (args.length != 2) {
            throw new BotException("this command allows only 1 argument - group name");
        }
        String groupName = args[1];
        Optional<Group> groupOpt = groupRepository.getById(groupName);
        if (groupOpt.isPresent()) {
            throw new BotException("Group with this name already exist");
        }
        if (!userRepository.getById(ownerChatId).isPresent()) {
            User user = new User();
            user.setChatId(ownerChatId);
            userRepository.createOrUpdate(user);
        }
        Group group = new Group();
        group.setName(groupName);
        group.setOwner(ownerChatId);
        groupRepository.createOrUpdate(group);
    }

}
