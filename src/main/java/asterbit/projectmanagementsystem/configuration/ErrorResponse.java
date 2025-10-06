package asterbit.projectmanagementsystem.configuration;

import java.util.Map;

public record ErrorResponse(
        String timestamp,
        int status,
        String message,
        String path,
        Map<String, String> details
) {}
