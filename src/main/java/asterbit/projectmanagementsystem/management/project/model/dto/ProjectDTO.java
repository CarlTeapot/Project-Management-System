package asterbit.projectmanagementsystem.management.project.model.dto;

import java.util.UUID;

public record ProjectDTO(
        UUID publicId,
        String name,
        String description,
        String managerEmail
) {
}
