package asterbit.projectmanagementsystem.security.model;

import asterbit.projectmanagementsystem.management.user.model.enums.Role;

import java.util.UUID;

public record PrincipalDetails(
        UUID publicId,
        Role role
) {
}
