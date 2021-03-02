package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.khnu.entity.UserDeadline;
import ua.khnu.entity.pk.UserDeadlinePK;

import java.util.List;

public interface UserDeadlineRepository extends JpaRepository<UserDeadline, UserDeadlinePK> {

    List<UserDeadline> findAllByIdDeadlineId(int deadlineId);

}
