package ua.khnu.entity;

import ua.khnu.entity.pk.SubscriptionPK;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @EmbeddedId
    private SubscriptionPK id;

    public long getUserChatId() {
        return id.getUserChatId();
    }

    public void setUserChatId(long userChatId) {
        id.setUserChatId(userChatId);
    }

    public String getGroup() {
        return id.getGroup();
    }

    public void setGroup(String group) {
        id.setGroup(group);
    }
}
