package com.example.identityservice.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateRoleRequest {
    private String name;
    private String description;
    private Map<String, List<String>> attributes;
}
