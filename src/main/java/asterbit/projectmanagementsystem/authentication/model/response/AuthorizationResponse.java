package asterbit.projectmanagementsystem.authentication.model.response;

import asterbit.projectmanagementsystem.management.invitation.model.dto.InvitationDTO;

import java.util.List;

public record AuthorizationResponse(
        String token,
        String expirationTime,
        List<InvitationDTO> invitations
) {
}
