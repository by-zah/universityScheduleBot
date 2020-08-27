package ua.khnu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "chat_id")
    private long chatId;
    @Column(name = "local")
    private String local;
    @Column(name = "interfaculty_discipline")
    private String interfacultyDiscipline;

    public long getChatId() {
        return chatId;
    }

    public String getLocal() {
        return local;
    }

    public String getInterfacultyDiscipline() {
        return interfacultyDiscipline;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setInterfacultyDiscipline(String interfacultyDiscipline) {
        this.interfacultyDiscipline = interfacultyDiscipline;
    }
}
