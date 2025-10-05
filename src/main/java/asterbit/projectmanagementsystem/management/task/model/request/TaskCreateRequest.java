package asterbit.projectmanagementsystem.management.task.model.request;

import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;

import java.time.LocalDateTime;

public record TaskCreateRequest(
        String title,
        String description,
        TaskPriority taskPriority,
        LocalDateTime dueDate,
        Long assignedUserId
) {}


