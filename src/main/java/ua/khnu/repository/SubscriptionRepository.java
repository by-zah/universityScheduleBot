package ua.khnu.repository;


import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Subscription;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class SubscriptionRepository extends AbstractRepository<Subscription> {

    @Autowired
    public SubscriptionRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    public Optional<Subscription> getByUserIdAndGroupName(Long userId, String groupName) {
        AtomicReference<Optional<Subscription>> res = new AtomicReference<>();
        transaction(session -> {
            Optional<Subscription> subscriptionOptional = session
                    .createQuery("FROM Subscription s WHERE s.user = :userId and s.group = :group"
                            , Subscription.class)
                    .setParameter("userId", userId)
                    .setParameter("group", groupName)
                    .uniqueResultOptional();
            res.set(subscriptionOptional);
        });
        return res.get();
    }

    public List<Subscription> getAllSubscriptionsByGroupName(String groupName) {
        AtomicReference<List<Subscription>> res = new AtomicReference<>();
        transaction(session -> {
            Query<Subscription> query = session
                    .createQuery("FROM Subscription s WHERE s.group = :groupName", Subscription.class);
            query.setParameter("groupName", groupName);
            res.set(query.list());
        });
        return res.get();
    }

    public List<Subscription> getAllByUserId(long userId) {
        AtomicReference<List<Subscription>> res = new AtomicReference<>();
        transaction(session -> {
            Query<Subscription> query = session
                    .createQuery("FROM Subscription s WHERE s.user = :userId", Subscription.class);
            query.setParameter("userId", userId);
            res.set(query.list());
        });
        return res.get();
    }

}
