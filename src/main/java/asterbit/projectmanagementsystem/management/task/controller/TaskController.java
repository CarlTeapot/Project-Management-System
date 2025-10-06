package asterbit.projectmanagementsystem.management.task.controller;

import asterbit.projectmanagementsystem.management.task.model.dto.TaskDTO;
import asterbit.projectmanagementsystem.management.task.model.request.TaskCreateRequest;
import asterbit.projectmanagementsystem.management.task.model.request.TaskUpdateRequest;
import asterbit.projectmanagementsystem.management.task.model.enums.Status;
import asterbit.projectmanagementsystem.management.task.model.enums.TaskPriority;
import asterbit.projectmanagementsystem.management.task.service.TaskService;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{publicId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create task (admin or project manager)")
    public ResponseEntity<TaskDTO> create(@PathVariable String publicId,
                                          @RequestBody TaskCreateRequest request,
                                          @AuthenticationPrincipal PrincipalDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(publicId, request, principal));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task (admin, project manager, or assignee)")
    public ResponseEntity<TaskDTO> update(@PathVariable String publicId,
                                          @PathVariable Long taskId,
                                          @RequestBody TaskUpdateRequest request,
                                          @AuthenticationPrincipal PrincipalDetails principal) {
        return ResponseEntity.ok(taskService.update(publicId, taskId, request, principal));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task (admin or project manager)")
    public ResponseEntity<Void> delete(@PathVariable String publicId,
                                       @PathVariable Long taskId,
                                       @AuthenticationPrincipal PrincipalDetails principal) {
        taskService.delete(publicId, taskId, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List project tasks (admin or any project member) with pagination and optional filters")
    public ResponseEntity<Page<TaskDTO>> list(@PathVariable String publicId,
                                              @RequestParam(required = false) Status status,
                                              @RequestParam(required = false) TaskPriority taskPriority,
                                              @PageableDefault(size = 20) Pageable pageable,
                                              @AuthenticationPrincipal PrincipalDetails principal) {
        return ResponseEntity.ok(taskService.listByProject(publicId, status, taskPriority, pageable, principal));
    }
}


