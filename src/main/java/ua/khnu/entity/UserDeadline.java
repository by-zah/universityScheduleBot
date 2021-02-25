package ua.khnu.entity;

import lombok.Getter;
import lombok.Setter;
import ua.khnu.entity.pk.UserDeadlinePK;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user_deadline")
public class UserDeadline {
    @EmbeddedId
    private UserDeadlinePK id;

    @Column(name = "is_done")
    private boolean isDone;

}
