package asterbit.projectmanagementsystem.management.project.repository;

import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByManager(User manager);

    Optional<Project> findByPublicId(UUID publicId);

}


