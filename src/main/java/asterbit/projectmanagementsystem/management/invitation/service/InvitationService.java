package asterbit.projectmanagementsystem.management.invitation.service;

import asterbit.projectmanagementsystem.security.model.PrincipalDetails;

import java.util.UUID;

public interface InvitationService {

    void sendInvitation(UUID projectPublicId, String userEmail, PrincipalDetails principalDetails);

    void acceptInvitation(UUID projectPublicId, PrincipalDetails principalDetails);

    void declineInvitation(UUID projectPublicId, PrincipalDetails principalDetails);

}

