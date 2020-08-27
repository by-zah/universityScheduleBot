package ua.khnu.repository;


import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Period;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class PeriodRepository extends AbstractRepository<Period> {

    @Autowired
    public PeriodRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Period> getByIds(int index, String groupName, DayOfWeek day) {
        AtomicReference<Optional<Period>> res = new AtomicReference<>();
        transaction(session -> {
            List<Period> list = session.byMultipleIds(Period.class).multiLoad(groupName, index, day);
            if (list.isEmpty()) {
                res.set(Optional.empty());
            } else {
                res.set(Optional.of(list.get(0)));
            }
        });
        return res.get();
    }
}
