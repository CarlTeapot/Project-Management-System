package asterbit.projectmanagementsystem.management.project.service.impl;

import asterbit.projectmanagementsystem.management.project.mapper.ProjectMapper;
import asterbit.projectmanagementsystem.management.project.model.dto.ProjectDTO;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.entity.ProjectMember;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.project.model.request.ProjectCreationRequest;
import asterbit.projectmanagementsystem.management.project.repository.ProjectRepository;
import asterbit.projectmanagementsystem.management.project.repository.ProjectMemberRepository;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.management.project.service.ProjectService;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectDTO create(ProjectCreationRequest projectRequest, PrincipalDetails details) {

        User owner = userRepository.findByPublicId(details.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + details.publicId()));

        Project project = new Project();
        project.setManager(owner);
        project.setPublicId(UUID.randomUUID());
        project.setName(projectRequest.name());
        project.setDescription(projectRequest.description());
        project.setCreateDate(LocalDateTime.now());
        project.setUpdateDate(LocalDateTime.now());

        Project saved = projectRepository.save(project);

        var pm = new ProjectMember();
        pm.setProject(saved);
        pm.setUser(owner);
        pm.setRole(ProjectRole.MANAGER);
        projectMemberRepository.save(pm);

        return projectMapper.toDto(saved);
    }


    @Override
    public ProjectDTO getByPublicId(String publicId, PrincipalDetails details) {

        Project project = projectRepository.findByPublicId(java.util.UUID.fromString(publicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + publicId));

        User user = userRepository.findByPublicId(details.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + details.publicId()));


        Optional<ProjectMember> requester = projectMemberRepository.findByProjectAndUser(project, user);

        if (requester.isEmpty() && details.role().equals(Role.USER)) {
            throw new SecurityException("Only project members or an admin can see the information about the project.");
        }

        return projectMapper.toDto(project);
    }

    @Override
    @Transactional
    public ProjectDTO update(String publicId, ProjectDTO updates, PrincipalDetails details) {
        Project project = projectRepository.findByPublicId(java.util.UUID.fromString(publicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + publicId));

        User user = userRepository.findByPublicId(details.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + details.publicId()));

        if (!project.getManager().getId().equals(user.getId()) && details.role() != Role.ADMIN) {
            throw new SecurityException("Only the project manager or an admin can update the project.");
        }

        if (updates.name() != null) project.setName(updates.name());
        if (updates.description() != null) project.setDescription(updates.description());
        project.setUpdateDate(java.time.LocalDateTime.now());

        Project saved = projectRepository.save(project);
        return projectMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(String projectPublicId, PrincipalDetails details) {

        Project project = projectRepository.findByPublicId(UUID.fromString(projectPublicId))
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectPublicId));

        User user = userRepository.findByPublicId(details.publicId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + details.publicId()));

        ProjectMember requester = projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + details.publicId()));

        if (!project.getManager().getId().equals(requester.getId()) && !details.role().equals(Role.ADMIN)) {
            throw new SecurityException("Only the project manager or an admin can delete the project.");
        }
        projectMemberRepository.deleteAllByProject(project);
        projectRepository.delete(project);
    }

}


