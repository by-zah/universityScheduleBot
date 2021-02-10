package ua.khnu.config;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ua.khnu.entity.*;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ua.khnu.repository")
public class HibernateConfig {

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(System.getenv("DATABASE_URL"));
    }


    @Bean
    public EntityManagerFactory entityManagerFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "org.postgresql.Driver");
        settings.put(Environment.URL, System.getenv("CONNECTION_URL"));
        settings.put(Environment.USER, System.getenv("DB_USER"));
        settings.put(Environment.PASS, System.getenv("DB_PASS"));
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL95Dialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.C3P0_MAX_SIZE, "19");
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

    @Bean
    @Autowired
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }
}
