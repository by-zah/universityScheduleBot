package ua.khnu.repository;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Period;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class PeriodRepository extends AbstractRepository<Period> {
    private static final Logger LOG = LogManager.getLogger(PeriodRepository.class);

    @Autowired
    public PeriodRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Period> getPeriodByDayAndIndex(int periodIndex, DayOfWeek dayOfWeek) {

        AtomicReference<List<Period>> res = new AtomicReference<>();
        transaction(session -> {
            Query<Period> query = session
                    .createQuery("FROM Period p WHERE p.index= :index and p.day= :day",
                            Period.class);
            int dayIndex = dayOfWeek.getValue();
            query.setParameter("index", periodIndex);
            query.setParameter("day", dayIndex);
            res.set(query.list());

        });
        return res.get();
    }
}
