package asterbit.projectmanagementsystem.management.project.controller;

import asterbit.projectmanagementsystem.management.project.model.dto.ProjectDTO;
import asterbit.projectmanagementsystem.management.project.model.request.ProjectCreationRequest;
import asterbit.projectmanagementsystem.management.project.service.ProjectService;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create project")
    public ResponseEntity<Void> create(@RequestBody ProjectCreationRequest request, @AuthenticationPrincipal PrincipalDetails principal) {

        projectService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get project by id")
    public ResponseEntity<ProjectDTO> get(@PathVariable String publicId) {
        return ResponseEntity.ok(projectService.getByPublicId(publicId));
    }

    @PutMapping("/{publicId}")
    @Operation(summary = "Update project")
    public ResponseEntity<ProjectDTO> update(@PathVariable String publicId,
                                             @RequestBody ProjectDTO request,
                                             @AuthenticationPrincipal PrincipalDetails principal) {
        return ResponseEntity.ok(projectService.update(publicId, request, principal));
    }

    @DeleteMapping("/{publicId}")
    @Operation(summary = "Delete project")
    public ResponseEntity<Void> delete(@PathVariable String publicId,
                                       @AuthenticationPrincipal PrincipalDetails principal) {
        projectService.delete(publicId, principal);
        return ResponseEntity.ok().build();
    }
}


