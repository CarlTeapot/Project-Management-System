package asterbit.projectmanagementsystem.management.task.model.entity;


import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority taskPriority;

    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        updateDate = createDate;
        if (status == null) {
            status = Status.TODO;
        }
        if (taskPriority == null) {
            taskPriority = TaskPriority.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}
