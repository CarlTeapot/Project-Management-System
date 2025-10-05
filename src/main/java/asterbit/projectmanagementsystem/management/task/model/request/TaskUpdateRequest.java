package asterbit.projectmanagementsystem.management.task.model.request;

import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;

import java.time.LocalDateTime;

public record TaskUpdateRequest(
        String title,
        String description,
        Status status,
        TaskPriority taskPriority,
        LocalDateTime dueDate,
        Long assignedUserId
) {}


