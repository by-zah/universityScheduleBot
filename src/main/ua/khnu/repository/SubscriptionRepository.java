package ua.khnu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.khnu.entity.pk.SubscriptionPK;
import ua.khnu.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionPK> {

}
