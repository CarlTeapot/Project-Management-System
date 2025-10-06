package asterbit.projectmanagementsystem.management.task.service;

import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.request.TaskCreateRequest;
import asterbit.projectmanagementsystem.management.task.model.request.TaskUpdateRequest;
import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskDTO create(String projectPublicId, TaskCreateRequest request, PrincipalDetails principal);

    TaskDTO update(String projectPublicId, Long taskId, TaskUpdateRequest request, PrincipalDetails principal);

    void delete(String projectPublicId, Long taskId, PrincipalDetails principal);

    Page<TaskDTO> listByProject(String projectPublicId,
                                Status status,
                                TaskPriority taskPriority,
                                Pageable pageable,
                                PrincipalDetails principal);
}
