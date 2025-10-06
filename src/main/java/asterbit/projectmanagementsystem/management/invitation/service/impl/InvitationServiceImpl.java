package asterbit.projectmanagementsystem.management.invitation.service.impl;

import asterbit.projectmanagementsystem.management.invitation.model.entity.ProjectInvitation;
import asterbit.projectmanagementsystem.management.invitation.model.enums.InvitationStatus;
import asterbit.projectmanagementsystem.management.invitation.repository.InvitationRepository;
import asterbit.projectmanagementsystem.management.invitation.service.InvitationService;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.entity.ProjectMember;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    @Transactional
    public void sendInvitation(UUID projectPublicId, String userEmail, PrincipalDetails principalDetails) {
        Project project = projectRepository.findByPublicId(projectPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        User inviter = userRepository.findByPublicId(principalDetails.publicId())
                .orElseThrow(() -> new IllegalArgumentException("Inviter not found: " + principalDetails.publicId()));

        if (user.getId().equals(inviter.getId())) {
            throw new IllegalArgumentException("Cannot invite yourself");
        }

        if (!project.getManager().getId().equals(inviter.getId()) && inviter.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Only project manager or admin can send invitations");
        }
        if (projectMemberRepository.findByProjectAndUser(project, user).isPresent()) {
            throw new IllegalArgumentException("User is already a member of the project");
        }

        invitationRepository.findByProjectAndUser(project, user)
                .ifPresent(inv -> { throw new IllegalArgumentException("Invitation already exists"); });

        ProjectInvitation invitation = new ProjectInvitation();
        invitation.setProject(project);
        invitation.setUser(user);
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public void acceptInvitation(UUID projectPublicId, PrincipalDetails principalDetails) {
        Project project = projectRepository.findByPublicId(projectPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        User user = userRepository.findByPublicId(principalDetails.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principalDetails.publicId()));

        ProjectInvitation invitation = invitationRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found for this user and project"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }

        if (projectMemberRepository.findByProjectAndUser(project, user).isEmpty()) {
            ProjectMember member = new ProjectMember();
            member.setProject(project);
            member.setUser(user);
            member.setRole(ProjectRole.COLLABORATOR);
            projectMemberRepository.save(member);
        }

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    @Override
    @Transactional
    public void declineInvitation(UUID projectPublicId, PrincipalDetails principalDetails) {
        Project project = projectRepository.findByPublicId(projectPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        User user = userRepository.findByPublicId(principalDetails.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + principalDetails.publicId()));

        ProjectInvitation invitation = invitationRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found for this user and project"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is not pending");
        }

        invitation.setStatus(InvitationStatus.DECLINED);
        invitationRepository.save(invitation);
    }
}


