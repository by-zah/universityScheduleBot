package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Group;
import ua.khnu.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User u JOIN Subscription  s on u.id = s.id.userChatId JOIN Group g on s.id.group = g.name WHERE g IN :groups AND u.settings.isDeadlineNotificationsEnabled = :isDeadlineNotificationsEnabled")
    List<User> findAllByGroupsContainingAndSettingsIsDeadlineNotificationsEnabled(@Param("groups") List<Group> groups, @Param("isDeadlineNotificationsEnabled") boolean isDeadlineNotificationsEnabled);

    @Query("FROM User u JOIN Subscription  s on u.id = s.id.userChatId WHERE s.id.group = :groupName")
    List<User> findAllByGroupName(@Param("groupName") String groupName);

    @Query("SELECT u.id FROM User u WHERE u.id IN :ids AND u.settings.isDeadlineNotificationsEnabled = :isDeadlineNotificationsEnabled")
    List<Integer> findAllByIdInAndSettingsIsDeadlineNotificationsEnabled(@Param("ids") Iterable<Long> ids, @Param("isDeadlineNotificationsEnabled") boolean isDeadlineNotificationsEnabled);
}
