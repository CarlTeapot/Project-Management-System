package asterbit.projectmanagementsystem.authentication.model.request;

public record RegistrationRequest(
        String email,
        String password,
        String confirmPassword
) {
}
