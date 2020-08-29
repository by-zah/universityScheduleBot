package ua.khnu.repository;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Group;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class GroupRepository extends AbstractRepository<Group> {

    @Autowired
    public GroupRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Group> getByGroupName(String groupName) {
        AtomicReference<Optional<Group>> res = new AtomicReference<>();
        transaction(session -> res.set(session.byId(Group.class).loadOptional(groupName)));
        return res.get();
    }

    public List<Group> getAllUserGroups(long userChatId) {
        AtomicReference<List<Group>> res = new AtomicReference<>();
        transaction(session -> {
            Query<Group> query = session.createQuery("FROM Group g WHERE g.owner= :userChatId", Group.class);
            query.setParameter("userChatId", userChatId);
            res.set(query.list());
        });
        return res.get();
    }
}
