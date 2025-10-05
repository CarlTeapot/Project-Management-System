package asterbit.projectmanagementsystem.management.task.service.impl;

import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import asterbit.projectmanagementsystem.management.task.model.request.TaskCreateRequest;
import asterbit.projectmanagementsystem.management.task.model.request.TaskUpdateRequest;
import asterbit.projectmanagementsystem.management.task.repository.TaskRepository;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.management.task.service.TaskService;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskDTO create(String projectPublicId, TaskCreateRequest request, PrincipalDetails principal) {
        Project project = projectRepository.findByPublicId(UUID.fromString(projectPublicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        User user = userRepository.findByPublicId(principal.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.publicId()));

        boolean isAdmin = principal.role() == Role.ADMIN;
        boolean isManager = projectMemberRepository.existsByProjectAndUserAndRole(project, user, ProjectRole.MANAGER);
        if (!isAdmin && !isManager) {
            throw new SecurityException("Only admin or project manager can create tasks");
        }

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setTaskPriority(request.taskPriority());
        task.setDueDate(request.dueDate());
        if (request.assignedUserId() != null) {
            User assignee = userRepository.findById(request.assignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found: " + request.assignedUserId()));
            task.setAssignedUser(assignee);
        }
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Override
    @Transactional
    public TaskDTO update(String projectPublicId, Long taskId, TaskUpdateRequest request, PrincipalDetails principal) {
        Project project = projectRepository.findByPublicId(UUID.fromString(projectPublicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        if (!task.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Task does not belong to project");
        }

        User user = userRepository.findByPublicId(principal.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.publicId()));

        boolean isAdmin = principal.role() == Role.ADMIN;
        boolean isManager = projectMemberRepository.existsByProjectAndUserAndRole(project, user, ProjectRole.MANAGER);
        boolean isAssignee = task.getAssignedUser() != null && task.getAssignedUser().getId().equals(user.getId());
        if (!(isAdmin || isManager || isAssignee)) {
            throw new SecurityException("Only admin, project manager, or assignee can update tasks");
        }

        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.status() != null) task.setStatus(request.status());
        if (request.taskPriority() != null) task.setTaskPriority(request.taskPriority());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());
        if (request.assignedUserId() != null) {
            User assignee = userRepository.findById(request.assignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found: " + request.assignedUserId()));
            task.setAssignedUser(assignee);
        }

        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(String projectPublicId, Long taskId, PrincipalDetails principal) {
        Project project = projectRepository.findByPublicId(UUID.fromString(projectPublicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        if (!task.getProject().getId().equals(project.getId())) {
            throw new IllegalArgumentException("Task does not belong to project");
        }

        User user = userRepository.findByPublicId(principal.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.publicId()));

        boolean isAdmin = principal.role() == Role.ADMIN;
        boolean isManager = projectMemberRepository.existsByProjectAndUserAndRole(project, user, ProjectRole.MANAGER);
        if (!isAdmin && !isManager) {
            throw new SecurityException("Only admin or project manager can delete tasks");
        }

        taskRepository.delete(task);
    }

    @Override
    public List<TaskDTO> listByProject(String projectPublicId, PrincipalDetails principal) {

        Project project = projectRepository.findByPublicId(UUID.fromString(projectPublicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        if (principal.role() != Role.ADMIN) {

            User user = userRepository.findByPublicId(principal.publicId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.publicId()));

            boolean isMember = projectMemberRepository.findByProjectAndUser(project, user).isPresent();

            if (!isMember) {
                throw new SecurityException("Only project members or admin can view tasks");
            }

        }
        return taskRepository.findByProject(project).stream().map(this::toDto).toList();
    }

    private TaskDTO toDto(Task t) {
        return new TaskDTO(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getTaskPriority(),
                t.getDueDate(),
                t.getProject().getId(),
                t.getAssignedUser() == null ? null : t.getAssignedUser().getId()
        );
    }
}


