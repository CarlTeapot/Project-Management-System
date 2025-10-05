package asterbit.projectmanagementsystem.authentication.model.request;

public record LoginRequest(
        String email,
        String password
) {}
