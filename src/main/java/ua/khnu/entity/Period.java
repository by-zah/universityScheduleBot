package ua.khnu.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "classes")
public class Period implements Serializable {
    @Id
    @Column(name = "group_name")
    private String groupName;

    @Id
    @Column(name = "index")
    private int index;

    @Id
    @Column(name = "day")
    private int day;

    @Column(name = "name")
    private String name;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "building")
    private String building;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type",length = 10)
    private PeriodType periodType;

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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

}
