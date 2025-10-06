package asterbit.projectmanagementsystem.management.invitation.controller;

import asterbit.projectmanagementsystem.management.invitation.service.InvitationService;
import asterbit.projectmanagementsystem.security.model.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Project invitations management")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    @Operation(summary = "Send invitation to a user by email")
    public ResponseEntity<Void> send(@RequestParam UUID projectPublicId,
                                     @RequestParam String userEmail,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.sendInvitation(projectPublicId, userEmail, principalDetails);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/accept")
    @Operation(summary = "Accept invitation for the given project")
    public ResponseEntity<Void> accept(@RequestParam UUID projectPublicId,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.acceptInvitation(projectPublicId, principalDetails);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/decline")
    @Operation(summary = "Decline invitation for the given project")
    public ResponseEntity<Void> decline(@RequestParam UUID projectPublicId,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        invitationService.declineInvitation(projectPublicId, principalDetails);
        return ResponseEntity.noContent().build();
    }
}


