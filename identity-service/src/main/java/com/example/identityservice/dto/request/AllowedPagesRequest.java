package com.example.identityservice.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AllowedPagesRequest {
    private List<String> roles;
}
