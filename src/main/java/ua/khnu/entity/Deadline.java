package ua.khnu.entity;

import lombok.*;
import ua.khnu.util.csv.CsvGetter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "deadline")
@ToString
public class Deadline {
    @Id
    @SequenceGenerator(name = "deadline_id_seq",
            sequenceName = "deadline_id_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "deadline_id_seq")
    private Integer id;
    @Column(name = "deadline_time")
    private LocalDateTime deadLineTime;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "task_description")
    private String taskDescription;
    @Column(name = "class_name")
    private String className;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_name", insertable = false, updatable = false)
    private Group relatedGroup;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "deadline_id", insertable = false, updatable = false)
    private List<UserDeadline> userDeadlines;

    @CsvGetter(order = 2)
    public LocalDateTime getDeadLineTime() {
        return ZonedDateTime.of(deadLineTime, ZoneId.of(TIME_ZONE_ID)).toLocalDateTime();
    }

    @CsvGetter(order = 0)
    public String getGroupName() {
        return groupName;
    }

    @CsvGetter(order = 3)
    public String getTaskDescription() {
        return taskDescription;
    }

    @CsvGetter(order = 1)
    public String getClassName() {
        return className;
    }

    public Optional<User> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }
}
