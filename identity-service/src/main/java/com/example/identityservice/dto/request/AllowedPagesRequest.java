package com.example.identityservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class AllowedPagesRequest {
    @NotEmpty(message = "Roles list must not be empty")
    private List<String> roles;
}
