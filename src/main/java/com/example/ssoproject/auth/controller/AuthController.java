package com.example.ssoproject.auth.controller;

import com.example.ssoproject.domain.entity.User;
import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        Map<String, Object> response = new HashMap<>();

        if (oAuth2User == null) {
            response.put("loggedIn", false);
            return response;
        }

        String provider = oAuth2User.getAuthorities()
                .stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", "").toLowerCase())
                .orElse("unknown");

        String socialId = switch (provider) {
            case "google" -> (String) oAuth2User.getAttributes().get("sub");
            case "naver" -> {
                Map<String, Object> naverRes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                yield (String) naverRes.get("id");
            }
            case "kakao" -> String.valueOf(oAuth2User.getAttributes().get("id"));
            default -> "unknown";
        };

        userRepository.findBySocialIdAndProvider(socialId, provider).ifPresentOrElse(user -> {
            response.put("loggedIn", true);
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("provider", user.getProvider());
            response.put("createdAt", user.getCreatedAt());
        }, () -> response.put("loggedIn", false));

        return response;
    }
}
