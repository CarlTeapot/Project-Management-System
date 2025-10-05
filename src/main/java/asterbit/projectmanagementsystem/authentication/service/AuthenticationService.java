package asterbit.projectmanagementsystem.authentication.service;

import asterbit.projectmanagementsystem.authentication.model.request.LoginRequest;
import asterbit.projectmanagementsystem.authentication.model.request.RegistrationRequest;
import asterbit.projectmanagementsystem.authentication.model.response.AuthorizationResponse;

public interface AuthenticationService {

    AuthorizationResponse register(RegistrationRequest request);

    AuthorizationResponse login(LoginRequest request);
}
