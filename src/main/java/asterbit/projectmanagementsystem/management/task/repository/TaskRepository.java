package asterbit.projectmanagementsystem.management.task.repository;

import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);

    Page<Task> findByProject(Project project, Pageable pageable);

    Page<Task> findByProjectAndStatus(Project project, Status status, Pageable pageable);

    Page<Task> findByProjectAndTaskPriority(Project project, TaskPriority taskPriority, Pageable pageable);

    Page<Task> findByProjectAndStatusAndTaskPriority(Project project, Status status, TaskPriority taskPriority, Pageable pageable);
}


