package ua.khnu.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.khnu.repository.util.Operation;

import java.util.List;

@Component
public abstract class AbstractRepository<T> {
    private static final Logger LOG = LogManager.getLogger(AbstractRepository.class);
    protected SessionFactory sessionFactory;

    @Autowired
    public AbstractRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected void transaction(Operation operation) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            operation.apply(session);
            transaction.commit();
        } catch (Exception e) {
            LOG.error(e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
            // throw new BotException("Error while data saving");
        }
    }

    public void createOrUpdate(T entity) {
        transaction(session -> session.saveOrUpdate(entity));
    }

    public void delete(T entity) {
        transaction(session -> session.remove(entity));
    }

    public void createAll(List<T> entities) {
        transaction(session -> entities.forEach(session::save));
    }

}
