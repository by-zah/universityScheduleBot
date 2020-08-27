package ua.khnu.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.User;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class UserRepository extends AbstractRepository<User> {

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> getById(Long chatId){
        AtomicReference<Optional<User>> res = new AtomicReference<>();
        transaction(session ->res.set(session.byId(User.class).loadOptional(chatId)));
        return res.get();
    }
}
