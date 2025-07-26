package com.example.ssoproject.domain.repository;

import com.example.ssoproject.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //중복 체크
    Optional<User> findBySocialIdAndProvider(String socialId, String provider);
    //관리자 페이지 검색 (이름/아이디)
    @Query("SELECT u FROM User u " +
            "WHERE (:keyword IS NULL OR " +
            "u.name LIKE %:keyword% OR " +
            "u.email LIKE %:keyword%)")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
