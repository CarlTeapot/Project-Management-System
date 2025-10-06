package asterbit.projectmanagementsystem.management.task.model.dto;

import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDTO(
        UUID publicId,
        String title,
        String description,
        Status status,
        TaskPriority taskPriority,
        LocalDateTime dueDate,
        UUID projectPublicId,
        String assignedUserEmail
) {}


