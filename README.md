## Project Management System

Java 17 Spring Boot application that manages projects, tasks, and invitations with JWT-based authentication and role-based authorization.

### Architecture Overview

- Global roles (system-wide):
  - ADMIN: Full access across projects; can manage any project, send invitations, and perform administrative actions.
  - USER: Regular authenticated user; access is scoped to projects where they are members or have been invited.

- Project roles (per-project):
  - MANAGER: The owner/manager of a project; can create/update/delete the project, invite users, and manage tasks within that project.
  - COLLABORATOR: A project member; can view project tasks and update status of tasks assigned to them.

Roles are enforced in services by checking the authenticated principal and project membership via `ProjectMember` and `ProjectRole`.

### Authentication and Security

- JWT-based stateless authentication using RS256 (RSA public/private keys).
- `JwtAuthenticationFilter` decodes JWT, extracts subject (user publicId) and roles and sets `PrincipalDetails` in the SecurityContext.
- Security rules:
  - `/api/auth/**` and Swagger endpoints are public.
  - `/api/admin/**` requires role ADMIN.
  - All other endpoints require authentication.

### Project System

- Create project: the authenticated user becomes the owner (manager) of the newly created project.
- Get/Update/Delete project: allowed for project manager or `ADMIN` (or view for members depending on service rules).

### Task System

- Create task: `ADMIN` or project `MANAGER` may create tasks in a project.
- Update task: `ADMIN`, project `MANAGER`, or the task assignee may update a task.
- Delete task: `ADMIN` or project `MANAGER` may delete tasks.
- List tasks: project `ADMIN` or any `ProjectMember` can list tasks. Pagination and filters are supported:
  - Query params: `status`, `taskPriority`, and `Pageable` (e.g., `page`, `size`, `sort`).

### Invitation System

- Send invitation: `ADMIN` or project `MANAGER` can invite a user by email to a project. Prevents duplicates and self-invites.
- Accept invitation: invited user joins the project as `COLLABORATOR` (if not already a member) and the invitation is marked `ACCEPTED`.
- Decline invitation: invited user can decline; invitation is marked `DECLINED`.

### Technology Stack

- Java 17, Spring Boot 3+
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- PostgreSQL (H2 can be used for dev with configuration changes)
- MapStruct (DTO mapping)
- Lombok
- Swagger/OpenAPI (Springdoc)
- JUnit + Mockito (unit tests)
- Docker

### Getting Started

1) Clone the repository

```bash
git clone https://github.com/your-org/project-management-system.git
cd project-management-system
```

2) Generate RSA key pair for JWT

```bash
# Generate private key (PKCS#8) and public key (PEM)
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 \
  | openssl pkcs8 -topk8 -nocrypt -outform DER \
  | base64 -w 0 > private.der.b64

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 \
  | tee >(openssl rsa -pubout -outform DER | base64 -w 0 > public.der.b64) \
  | openssl pkcs8 -topk8 -nocrypt -outform DER | base64 -w 0 > private.der.b64


# Export keys as environment variables (Linux/macOS)
export ASTERBIT_JWT_PRIVATE_KEY="$(cat private.der.b64)"
export ASTERBIT_JWT_PUBLIC_KEY="$(cat public.der.b64)"
```

Keys used in `src/main/resources/application.properties`:

- `jwt.public-key=${ASTERBIT_JWT_PUBLIC_KEY}`
- `jwt.private-key=${ASTERBIT_JWT_PRIVATE_KEY}`

Corresponding configuration properties class: `JwtConfigurationProperties` with prefix `jwt`.

3) Set admin password (optional; default is `admin123` in dev). The seeder reads `ADMIN_PASSWORD` env variable.

```bash
export ADMIN_PASSWORD="ChangeThisStrongPassword"
```

4) Configure database (PostgreSQL default)

Defaults in `application.properties`:

- `spring.datasource.url=jdbc:postgresql://localhost:5452/asterbit_db`
- `spring.datasource.username=root`
- `spring.datasource.password=asterbit`

Adjust these as needed, or point to an H2 configuration if desired.
 
Instead of manually setting up a database, you can run docker, in which case
Docker compose will take care of the rest.

Here is the tutorial to install Docker:
https://docs.docker.com/engine/install/


Default port is 8081, so ensure it's free or change in `application.properties`.

5) Run the application

```bash
./gradlew bootRun
# or build and run the jar
./gradlew build
java -jar build/libs/project-management-system-0.0.1-SNAPSHOT.jar
```

6) Explore API

- Swagger UI: `http://localhost:8081/swagger-ui.html`

### Manual Tests (Shell Scripts)

See `manual_tests/` for ready-made curl scripts:
If you don't have mac or linux, you can use git bash on windows to run these scripts.

- Auth: `register.sh`, `login.sh`
- Projects: `create_project.sh`, `get_project.sh`, `update_project.sh`, `delete_project.sh`
- Tasks: `create_task.sh`, `update_task.sh`, `delete_task.sh`, `list_tasks.sh` (supports pagination/filters)
- Invitations: `send_invitation.sh`, `accept_invitation.sh`, `decline_invitation.sh`

All scripts expect a JWT token in `manual_tests/token.txt` (generated via `login.sh`).

### Configuration Keys Reference

From `application.properties` and `JwtConfigurationProperties`:

- JWT keys
  - Property keys:
    - `jwt.public-key`
    - `jwt.private-key`
  - Environment variables (referenced in properties):
    - `ASTERBIT_JWT_PUBLIC_KEY`
    - `ASTERBIT_JWT_PRIVATE_KEY`

- JWT metadata
  - `jwt.expiration-millis` (default `3600000`)
  - `jwt.issuer` (default `asterbit-project-management`)
  - `jwt.audience` (default `project-management-users`)

- Admin seeding
  - Environment variable: `ADMIN_PASSWORD`

### Notes

- Ensure RSA keys are exported without extra formatting issues; multiline env vars are supported as shown.
- For production, set a secure `ADMIN_PASSWORD` and consider a persistent database.

# Project-Management-System