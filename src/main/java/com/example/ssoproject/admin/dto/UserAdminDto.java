package com.example.ssoproject.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAdminDto {
    private Long id;
    private String name;
    private String email;
    private String socialId;
    private Integer age;
    private String provider;
    private String gender;
    private LocalDateTime createdAt;
}
