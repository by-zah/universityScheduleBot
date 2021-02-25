package ua.khnu.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "groups")
public class Group {

    @Id
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User owner;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private List<User> students;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_name", insertable = false, updatable = false)
    private List<Period> periods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "name", insertable = false, updatable = false)
    private Deadline deadline;
}
