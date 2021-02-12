package ua.khnu.entity.pk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscriptionPK implements Serializable {
    @Column(name = "user_chat_id")
    private long userChatId;

    @Column(name = "\"group\"")
    private String group;

    public SubscriptionPK() {
    }

    public SubscriptionPK(long userChatId, String group) {
        this.userChatId = userChatId;
        this.group = group;
    }

    public long getUserChatId() {
        return userChatId;
    }

    public void setUserChatId(long userChatId) {
        this.userChatId = userChatId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPK that = (SubscriptionPK) o;
        return userChatId == that.userChatId &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userChatId, group);
    }
}
