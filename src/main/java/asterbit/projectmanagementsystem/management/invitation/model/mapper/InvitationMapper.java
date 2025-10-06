package asterbit.projectmanagementsystem.management.invitation.model.mapper;

import asterbit.projectmanagementsystem.management.invitation.model.dto.InvitationDTO;
import asterbit.projectmanagementsystem.management.invitation.model.entity.ProjectInvitation;
import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    @Mapping(source = "project.publicId", target = "projectPublicId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "project.manager.email", target = "invitedBy")
    @Mapping(source = "createDate", target = "invitationDate")
    InvitationDTO toDto(ProjectInvitation invitation);
}
