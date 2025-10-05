package asterbit.projectmanagementsystem.management.task.service;

import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.request.TaskCreateRequest;
import asterbit.projectmanagementsystem.management.task.model.request.TaskUpdateRequest;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;

import java.util.List;

public interface TaskService {

    TaskDTO create(String projectPublicId, TaskCreateRequest request, PrincipalDetails principal);

    TaskDTO update(String projectPublicId, Long taskId, TaskUpdateRequest request, PrincipalDetails principal);

    void delete(String projectPublicId, Long taskId, PrincipalDetails principal);

    List<TaskDTO> listByProject(String projectPublicId, PrincipalDetails principal);
}


