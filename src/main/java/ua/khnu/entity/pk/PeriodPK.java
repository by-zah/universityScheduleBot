package ua.khnu.entity.pk;

import ua.khnu.entity.PeriodType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Objects;

@Embeddable
public class PeriodPK implements Serializable {

    @Column(name = "group_name")
    private String groupName;

    private int index;

    @Column(name = "day", length = 10)
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @Column(name = "period_type", length = 10)
    @Enumerated(EnumType.STRING)
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

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodPK periodPK = (PeriodPK) o;
        return index == periodPK.index &&
                Objects.equals(groupName, periodPK.groupName) &&
                day == periodPK.day &&
                periodType == periodPK.periodType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupName, index, day, periodType);
    }
}
