package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.Group;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, String> {

    List<Group> findAllByStudentsIdIn(List<Long> studentsIds);
}
