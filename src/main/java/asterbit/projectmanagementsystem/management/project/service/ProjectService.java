package asterbit.projectmanagementsystem.management.project.service;

import asterbit.projectmanagementsystem.management.project.model.dto.ProjectDTO;
import asterbit.projectmanagementsystem.management.project.model.request.ProjectCreationRequest;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
public interface ProjectService {

    ProjectDTO create(ProjectCreationRequest project, PrincipalDetails principal);

    ProjectDTO getByPublicId(String publicId, PrincipalDetails details);

    ProjectDTO update(String publicId, ProjectDTO updates, PrincipalDetails principal);

    void delete(String projectPublicId, PrincipalDetails details);
}
