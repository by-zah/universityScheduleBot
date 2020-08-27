package ua.khnu.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subscriptions")
public class Subscription implements Serializable {

    @Id
    @Column(name = "\"user\"")
    private long user;

    @Id
    @Column(name = "\"group\"")
    private String group;

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
