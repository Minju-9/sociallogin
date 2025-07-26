package com.example.ssoproject.domain.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "memberinfo")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK 순번

    @Column
    private String socialId; // 고유 ID

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String provider; //google / kakao / naver / facebook

    @Column
    private String gender; //(F / M / null)
    private Integer age;

    @CreationTimestamp
    private LocalDateTime createdAt; // 가입일
}
