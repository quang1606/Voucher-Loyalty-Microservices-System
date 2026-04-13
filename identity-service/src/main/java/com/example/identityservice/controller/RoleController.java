package com.example.identityservice.controller;

import com.example.common.BaseErrorCode;
import com.example.common.BaseResponse;
import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.request.UpdateRoleAttributesRequest;
import com.example.identityservice.dto.request.UpdateRoleRequest;
import com.example.identityservice.dto.response.RoleDetailResponse;
import com.example.identityservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RoleDetailResponse>>> getAllRoles() {
        return ResponseEntity.ok(BaseResponse.<List<RoleDetailResponse>>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(roleService.getAllRoles())
                .build());
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createRole(@RequestBody CreateRoleRequest request) {
        roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<BaseResponse<RoleDetailResponse>> getRole(@PathVariable String roleName) {
        return ResponseEntity.ok(BaseResponse.<RoleDetailResponse>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .data(roleService.getRole(roleName))
                .build());
    }

    @PutMapping("/{roleName}")
    public ResponseEntity<BaseResponse<Void>> updateRole(@PathVariable String roleName, @RequestBody UpdateRoleRequest request) {
        roleService.updateRole(roleName, request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<BaseResponse<Void>> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }

    @PutMapping("/{roleName}/attributes")
    public ResponseEntity<BaseResponse<Void>> updateAttributes(@PathVariable String roleName, @RequestBody UpdateRoleAttributesRequest request) {
        roleService.updateRoleAttributes(roleName, request);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(BaseErrorCode.SUCCESS.getErrorCode())
                .status(BaseErrorCode.SUCCESS.getErrorNumCode())
                .message(BaseErrorCode.SUCCESS.getErrorDescription())
                .build());
    }
}
