package asterbit.projectmanagementsystem.management.project.model.dto;

public record ProjectDTO(
        Long id,
        String name,
        String description,
        Long managerId
) {
}
