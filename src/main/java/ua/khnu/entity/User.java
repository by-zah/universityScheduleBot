package ua.khnu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
