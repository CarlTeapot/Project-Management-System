package asterbit.projectmanagementsystem.management.task.model.dto;

import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;

import java.time.LocalDateTime;

public record TaskDTO(
        Long id,
        String title,
        String description,
        Status status,
        TaskPriority taskPriority,
        LocalDateTime dueDate,
        Long projectId,
        Long assignedUserId
) {}


