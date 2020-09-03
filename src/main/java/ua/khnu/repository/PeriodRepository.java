package ua.khnu.repository;


import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Period;
import ua.khnu.entity.PeriodType;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class PeriodRepository extends AbstractRepository<Period> {

    private static final String QUERY = "FROM Period p WHERE p.index= :index and p.day= :day and" +
            " (p.periodType= :pType1 or p.periodType= :pType2)";

    @Autowired
    public PeriodRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Period> getPeriodByDayAndIndexAndPeriodTypes(int periodIndex, DayOfWeek dayOfWeek,
                                                             PeriodType periodType1,PeriodType periodType2) {
        AtomicReference<List<Period>> res = new AtomicReference<>();
        transaction(session -> {
            Query<Period> query = session
                    .createQuery(QUERY,
                            Period.class);
            int dayIndex = dayOfWeek.getValue();
            query.setParameter("index", periodIndex);
            query.setParameter("day", dayIndex);
            query.setParameter("pType1",periodType1);
            query.setParameter("pType2",periodType2);
            res.set(query.list());

        });
        return res.get();
    }
}
