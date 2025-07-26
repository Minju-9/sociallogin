package com.example.ssoproject.admin.service;

import com.example.ssoproject.admin.dto.UserAdminDto;
import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public Page<UserAdminDto> getUsers(String keyword, String sortField, String direction, int page, int size){
        String searchKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        List<String>allowedFields = List.of("name", "email", "gender", "age", "createdAt");
        if (!allowedFields.contains(sortField)) {
            sortField = "createdAt";
        }
            Sort.Direction sortDirection =
                    "asc".equalsIgnoreCase(direction)?Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));

            return userRepository.searchUsers(searchKeyword,pageable)
                    .map(user -> UserAdminDto.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail() != null ? user.getEmail() : "-")
                            .provider(user.getProvider())
                            .gender(user.getGender())
                            .socialId(user.getSocialId())
                            .age(user.getAge())
                            .createdAt(user.getCreatedAt())
                            .build());
    }
}
