package ua.khnu.entity.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserDeadlinePK implements Serializable {
    @Column(name = "user_id")
    private int userId;
    @Column(name = "deadline_id")
    private int deadlineId;
}
