package asterbit.projectmanagementsystem.management.task.service;

import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.task.mapper.TaskMapper;
import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import asterbit.projectmanagementsystem.management.task.model.request.TaskCreateRequest;
import asterbit.projectmanagementsystem.management.task.service.impl.TaskServiceImpl;
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
class TaskServiceImplTest {

    @Mock private asterbit.projectmanagementsystem.management.task.repository.TaskRepository taskRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository projectMemberRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskMapper taskMapper;

    @InjectMocks private TaskServiceImpl service;

    @Test
    void create_asManager_createsTask() {
        String publicId = UUID.randomUUID().toString();
        PrincipalDetails principal = new PrincipalDetails(UUID.randomUUID(), Role.USER);
        Project project = new Project();
        User manager = User.builder().id(10L).role(Role.USER).build();

        when(projectRepository.findByPublicId(UUID.fromString(publicId))).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(principal.publicId())).thenReturn(Optional.of(manager));
        when(projectMemberRepository.existsByProjectAndUserAndRole(project, manager, ProjectRole.MANAGER)).thenReturn(true);
        when(userRepository.findByEmail("e@x.com")).thenReturn(Optional.of(User.builder().id(2L).build()));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        when(taskMapper.toDto(any(Task.class))).thenReturn(new TaskDTO(UUID.randomUUID(), "t", "d", null, null, null, null, null));

        TaskCreateRequest req = new TaskCreateRequest("t", "d", null, null, "e@x.com");
        TaskDTO dto = service.create(publicId, req, principal);

        assertThat(dto).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void create_throwsWhenNotManagerOrAdmin() {
        String publicId = UUID.randomUUID().toString();
        PrincipalDetails principal = new PrincipalDetails(UUID.randomUUID(), Role.USER);
        Project project = new Project();
        User user = User.builder().id(10L).role(Role.USER).build();

        when(projectRepository.findByPublicId(UUID.fromString(publicId))).thenReturn(Optional.of(project));
        when(userRepository.findByPublicId(principal.publicId())).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectAndUserAndRole(project, user, ProjectRole.MANAGER)).thenReturn(false);

        TaskCreateRequest req = new TaskCreateRequest("t", "d", null, null, "e@x.com");
        assertThrows(SecurityException.class, () -> service.create(publicId, req, principal));
    }
}
