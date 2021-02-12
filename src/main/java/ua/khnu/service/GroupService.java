package ua.khnu.service;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.khnu.entity.Group;
import ua.khnu.entity.User;
import ua.khnu.exception.BotException;
import ua.khnu.repository.GroupRepository;
import ua.khnu.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createNewGroup(int userId, String message) {
        String[] args = message.split(" ");
        if (args.length != 2) {
            throw new BotException("this command allows only 1 argument - group name");
        }
        String groupName = args[1];
        if (groupRepository.existsById(groupName)) {
            throw new BotException("Group with this name already exist");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BotException("User doesn't exist"));
        Group group = new Group();
        group.setName(groupName);
        group.setOwner(user);
        groupRepository.save(group);
    }

    @Transactional
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional
    public List<Group> getUserGroups(int userId) {
        return groupRepository.findAllByStudentsIdIn(ImmutableList.of(userId));
    }
}
