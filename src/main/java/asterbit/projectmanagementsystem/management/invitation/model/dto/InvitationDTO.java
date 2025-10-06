package asterbit.projectmanagementsystem.management.invitation.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvitationDTO(
        UUID projectPublicId,
        String projectName,
        String invitedBy,
        LocalDateTime invitationDate
) {
}
