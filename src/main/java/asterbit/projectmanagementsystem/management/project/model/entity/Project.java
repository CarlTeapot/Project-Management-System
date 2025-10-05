package asterbit.projectmanagementsystem.management.project.model.entity;


import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;
}