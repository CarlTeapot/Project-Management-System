package asterbit.projectmanagementsystem.management.repository;

import asterbit.projectmanagementsystem.management.model.entity.Project;
import asterbit.projectmanagementsystem.management.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByManager(User manager);
}


