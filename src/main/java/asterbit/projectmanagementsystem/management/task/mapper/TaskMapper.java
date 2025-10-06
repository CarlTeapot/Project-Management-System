package asterbit.projectmanagementsystem.management.task.mapper;

import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "project.publicId", target = "projectPublicId")
    @Mapping(source = "assignedUser.email", target = "assignedUserEmail")
    TaskDTO toDto(Task task);
}
