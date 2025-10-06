package asterbit.projectmanagementsystem.management.project.service;

import asterbit.projectmanagementsystem.management.project.mapper.ProjectMapper;
import asterbit.projectmanagementsystem.management.project.model.dto.ProjectDTO;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.entity.ProjectMember;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.project.model.request.ProjectCreationRequest;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.project.service.impl.ProjectServiceImpl;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl service;

    @Test
    void create_setsManager_andReturnsDto() {
        PrincipalDetails principal = new PrincipalDetails(UUID.randomUUID(), Role.USER);
        User owner = User.builder().id(1L).email("m@e.com").role(Role.USER).build();
        when(userRepository.findByPublicId(principal.publicId())).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));
        when(projectMapper.toDto(any(Project.class))).thenReturn(new ProjectDTO(UUID.randomUUID(), "n", "d", "m@e.com"));

        ProjectCreationRequest req = new ProjectCreationRequest("n", "d");
        ProjectDTO dto = service.create(req, principal);

        assertThat(dto).isNotNull();
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    void update_throwsWhenNotManagerOrAdmin() {
        UUID pid = UUID.randomUUID();
        Project project = new Project();
        User user = User.builder().id(2L).role(Role.USER).build();
        User user2 = User.builder().id(3L).role(Role.USER).build();
        project.setManager(user2);
        PrincipalDetails principal = new PrincipalDetails(UUID.randomUUID(), Role.USER);
        when(projectRepository.findByPublicId(pid)).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(principal.publicId())).thenReturn(Optional.of(user));
        assertThrows(SecurityException.class, () -> service.update(pid.toString(), new ProjectDTO(pid, "xd", null, null), principal));
    }

    @Test
    void delete_throwsWhenNotManagerOrAdmin() {
        UUID pid = UUID.randomUUID();
        Project project = new Project();
        User user = User.builder().id(2L).role(Role.USER).build();
        User user2 = User.builder().id(3L).role(Role.USER).build();
        project.setManager(user2);
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(ProjectRole.COLLABORATOR);
        PrincipalDetails principal = new PrincipalDetails(UUID.randomUUID(), Role.USER);
        when(projectRepository.findByPublicId(pid)).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(principal.publicId())).thenReturn(Optional.of(user));
        when(projectMemberRepository.findByProjectAndUser(project, user)).thenReturn(Optional.of(member));
        assertThrows(SecurityException.class, () -> service.delete(pid.toString(), principal));
    }
}
