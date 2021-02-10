package ua.khnu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
