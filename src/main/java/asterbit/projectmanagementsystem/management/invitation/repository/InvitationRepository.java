package asterbit.projectmanagementsystem.management.invitation.repository;

import asterbit.projectmanagementsystem.management.invitation.model.entity.ProjectInvitation;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<ProjectInvitation, Long> {

    Optional<ProjectInvitation> findByProjectAndUser(Project project, User user);
}


