package asterbit.projectmanagementsystem.authentication.model.response;

public record AuthorizationResponse(
        String token,
        String expirationTime
) {
}
