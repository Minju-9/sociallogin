package com.example.ssoproject.admin.controller;

import com.example.ssoproject.admin.dto.UserAdminDto;
import com.example.ssoproject.admin.service.AdminService;
import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminCotroller {
    private final AdminService adminService;
    @GetMapping("/admin/users")
    public Page<UserAdminDto> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminService.getUsers(keyword, sortField, direction, page, size);
    }
}
