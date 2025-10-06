package asterbit.projectmanagementsystem.management.project.mapper;

import asterbit.projectmanagementsystem.management.project.model.dto.ProjectDTO;
import asterbit.projectmanagementsystem.management.project.model.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "manager.id", target = "managerId")
    ProjectDTO toDto(Project project);
}
