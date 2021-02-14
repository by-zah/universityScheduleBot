package ua.khnu.entity;

import ua.khnu.entity.pk.PeriodPK;

import javax.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek;

@Entity
@Table(name = "classes")
public class Period implements Serializable {
    @EmbeddedId
    private final PeriodPK id;

    @Column(name = "name")
    private String name;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "building")
    private String building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_name", insertable = false, updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index", insertable = false, updatable = false)
    private ScheduleUnit scheduleUnit;

    public Period() {
        this.id = new PeriodPK();
    }

    public Group getGroup() {
        return group;
    }

    public ScheduleUnit getScheduleUnit() {
        return scheduleUnit;
    }

    public PeriodType getPeriodType() {
        return id.getPeriodType();
    }

    public void setPeriodType(PeriodType periodType) {
        id.setPeriodType(periodType);
    }

    public String getGroupName() {
        return id.getGroupName();
    }

    public void setGroupName(String groupName) {
        id.setGroupName(groupName);
    }

    public int getIndex() {
        return id.getIndex();
    }

    public void setIndex(int index) {
        id.setIndex(index);
    }

    public DayOfWeek getDay() {
        return id.getDay();
    }

    public void setDay(DayOfWeek day) {
        id.setDay(day);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public String toString() {
        return "Period{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", building='" + building + '\'' +
                ", group=" + group +
                ", scheduleUnit=" + scheduleUnit +
                '}';
    }
}
