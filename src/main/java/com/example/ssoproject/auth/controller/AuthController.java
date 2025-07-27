package com.example.ssoproject.auth.controller;

import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/auth/user")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User,
                                              OAuth2AuthenticationToken authentication) {
        Map<String, Object> response = new HashMap<>();

        if (oAuth2User == null) {
            response.put("loggedIn", false);
            response.put("name", "로그인 필요");
            return response;
        }

        // ✅ Spring Security가 관리하는 provider 이름 가져오기
        String provider = authentication.getAuthorizedClientRegistrationId(); // google/naver/kakao/facebook

        String socialId = null;
        if (provider.equals("google")) {
            socialId = (String) oAuth2User.getAttributes().get("sub");
        } else if (provider.equals("naver")) {
            Map<String, Object> naverRes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            socialId = (String) naverRes.get("id");
        } else if (provider.equals("kakao")) {
            socialId = String.valueOf(oAuth2User.getAttributes().get("id"));
        } else if (provider.equals("facebook")) {
            socialId = (String) oAuth2User.getAttributes().get("id");
        }

        // ✅ DB 조회 후 응답
        userRepository.findBySocialIdAndProvider(socialId, provider).ifPresentOrElse(user -> {
            response.put("loggedIn", true);
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("provider", user.getProvider());
            response.put("createdAt", user.getCreatedAt());
        }, () -> {
            response.put("loggedIn", false);
            response.put("name", "이름 없음");
        });

        return response;
    }
}
