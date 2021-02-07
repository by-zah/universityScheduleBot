package ua.khnu.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private int id;

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

    public List<Group> getGroupsUserOwn() {
        return groupsUserOwn;
    }

    public void setGroupsUserOwn(List<Group> groupsUserOwn) {
        this.groupsUserOwn = groupsUserOwn;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getInterfacultyDiscipline() {
        return interfacultyDiscipline;
    }

    public void setInterfacultyDiscipline(String interfacultyDiscipline) {
        this.interfacultyDiscipline = interfacultyDiscipline;
    }

    public boolean isSupper() {
        return isSupper;
    }

    public void setSupper(boolean isSupper) {
        this.isSupper = isSupper;
    }
}
