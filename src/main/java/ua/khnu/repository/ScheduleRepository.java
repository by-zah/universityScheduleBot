package ua.khnu.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.ScheduleUnit;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class ScheduleRepository extends AbstractRepository<ScheduleUnit> {

    @Autowired
    public ScheduleRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void clear() {
        transaction(session -> session.createQuery("DELETE FROM ScheduleUnit").executeUpdate());
    }

    public List<ScheduleUnit> getAll() {
        AtomicReference<List<ScheduleUnit>> res = new AtomicReference<>();
        transaction(session -> res.set(session.createQuery("FROM ScheduleUnit",ScheduleUnit.class).list()));
        return res.get();
    }
}
