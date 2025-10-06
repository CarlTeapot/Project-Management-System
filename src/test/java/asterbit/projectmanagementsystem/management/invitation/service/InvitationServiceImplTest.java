package asterbit.projectmanagementsystem.management.invitation.service;

import asterbit.projectmanagementsystem.management.invitation.model.entity.ProjectInvitation;
import asterbit.projectmanagementsystem.management.invitation.model.enums.InvitationStatus;
import asterbit.projectmanagementsystem.management.invitation.repository.InvitationRepository;
import asterbit.projectmanagementsystem.management.invitation.service.impl.InvitationServiceImpl;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.entity.ProjectMember;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceImplTest {

    @Mock
    private InvitationRepository invitationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @InjectMocks
    private InvitationServiceImpl service;

    @Test
    void sendInvitation_createsPendingInvitation() {
        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        User invitee = User.builder().id(2L).role(Role.USER).build();
        User inviter = User.builder().id(1L).role(Role.ADMIN).build();
        project.setManager(inviter);
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(invitee));
        when(userRepository.findByPublicId(any())).thenReturn(Optional.of(inviter));
        when(projectMemberRepository.findByProjectAndUser(project, invitee)).thenReturn(Optional.empty());
        when(invitationRepository.findByProjectAndUser(project, invitee)).thenReturn(Optional.empty());

        service.sendInvitation(projectPublicId, "user@example.com", new PrincipalDetails(UUID.randomUUID(), Role.ADMIN));

        ArgumentCaptor<ProjectInvitation> cap = ArgumentCaptor.forClass(ProjectInvitation.class);
        verify(invitationRepository).save(cap.capture());
        assertThat(cap.getValue().getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    void sendInvitation_throwsIfAlreadyMember() {
        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        User invitee = User.builder().id(2L).role(Role.USER).build();
        User inviter = User.builder().id(1L).role(Role.ADMIN).build();
        project.setManager(inviter);
        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(invitee));
        when(userRepository.findByPublicId(any())).thenReturn(Optional.of(inviter));
        when(projectMemberRepository.findByProjectAndUser(project, invitee)).thenReturn(Optional.of(new ProjectMember()));

        assertThrows(IllegalArgumentException.class, () -> service.sendInvitation(projectPublicId,
                "user@example.com",
                new PrincipalDetails(UUID.randomUUID(),
                        Role.ADMIN)));
    }

    @Test
    void acceptInvitation_addsMember_andMarksAccepted() {
        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        User user = User.builder().id(2L).role(Role.USER).build();
        ProjectInvitation inv = new ProjectInvitation();
        inv.setProject(project);
        inv.setUser(user);
        inv.setStatus(InvitationStatus.PENDING);

        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(any())).thenReturn(Optional.of(user));
        when(invitationRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(inv));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.empty());

        service.acceptInvitation(projectPublicId, new PrincipalDetails(UUID.randomUUID(), Role.USER));

        verify(projectMemberRepository).save(any(ProjectMember.class));
        assertThat(inv.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
    }

    @Test
    void declineInvitation_marksDeclined() {
        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        User user = User.builder().id(2L).role(Role.USER).build();
        ProjectInvitation inv = new ProjectInvitation();
        inv.setProject(project);
        inv.setUser(user);
        inv.setStatus(InvitationStatus.PENDING);

        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(any())).thenReturn(Optional.of(user));
        when(invitationRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(inv));

        service.declineInvitation(projectPublicId, new PrincipalDetails(UUID.randomUUID(), Role.USER));

        assertThat(inv.getStatus()).isEqualTo(InvitationStatus.DECLINED);
        verify(invitationRepository).save(inv);
    }

    @Test
    void declineInvitation_throwsIfNotPending() {
        UUID projectPublicId = UUID.randomUUID();
        Project project = new Project();
        User user = User.builder().id(2L).role(Role.USER).build();
        ProjectInvitation inv = new ProjectInvitation();
        inv.setProject(project);
        inv.setUser(user);
        inv.setStatus(InvitationStatus.ACCEPTED);

        when(projectRepository.findByPublicId(projectPublicId)).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(any())).thenReturn(Optional.of(user));
        when(invitationRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(inv));

        assertThrows(IllegalArgumentException.class, () ->
                service.declineInvitation(projectPublicId, new PrincipalDetails(UUID.randomUUID(), Role.USER)));
        verify(invitationRepository, never()).save(any());
    }
}
