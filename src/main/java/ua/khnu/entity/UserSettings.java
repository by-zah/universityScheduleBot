package ua.khnu.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
public class UserSettings {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "is_class_notifications_enabled")
    private boolean isClassNotificationsEnabled;

    @Column(name = "is_deadline_notifications_enabled")
    private boolean isDeadlineNotificationsEnabled;

    public UserSettings() {
        isClassNotificationsEnabled = true;
        isDeadlineNotificationsEnabled = true;
    }
}
