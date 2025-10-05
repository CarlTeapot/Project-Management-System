package asterbit.projectmanagementsystem.management.project.repository;

import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.project.model.entity.ProjectMember;
import asterbit.projectmanagementsystem.management.project.model.enums.ProjectRole;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectAndUserAndRole(Project project, User user, ProjectRole role);

    Optional<ProjectMember> findByProjectAndUser(Project project, User user);
}


