package ua.khnu.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "local")
    private String local;

    @Column(name = "interfaculty_discipline")
    private String interfacultyDiscipline;

    @Column(name = "is_supper")
    private boolean isSupper;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subscriptions",
            joinColumns = {@JoinColumn(name = "user_chat_id")},
            inverseJoinColumns = {@JoinColumn(name = "\"group\"")}
    )
    private List<Group> groups;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<Group> groupsUserOwn;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "user_id")
    private UserSettings settings;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private List<UserDeadline> deadlines;
    
    public User() {
        settings = new UserSettings();
    }

    public User(long id) {
        this();
        this.id = id;
    }
}
