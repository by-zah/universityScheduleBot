package ua.khnu.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.khnu.entity.*;

import java.util.Properties;

@Configuration
public class HibernateConfig {


    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "org.postgresql.Driver");
        settings.put(Environment.URL, System.getenv("CONNECTION_URL"));
        settings.put(Environment.USER, System.getenv("DB_USER"));
        settings.put(Environment.PASS, System.getenv("DB_PASS"));
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL95Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "create-drop");
        configuration.setProperties(settings);

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Group.class);
        configuration.addAnnotatedClass(Subscription.class);
        configuration.addAnnotatedClass(ScheduleUnit.class);
        configuration.addAnnotatedClass(Period.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
