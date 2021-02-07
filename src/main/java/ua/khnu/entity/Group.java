package ua.khnu.entity;

import javax.persistence.*;
import java.util.List;

@Entity
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

    public List<Period> getPeriods() {
        return periods;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
