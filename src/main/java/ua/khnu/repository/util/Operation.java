package ua.khnu.repository.util;

import org.hibernate.Session;

@FunctionalInterface
public interface Operation {
    void apply(Session session);
}
