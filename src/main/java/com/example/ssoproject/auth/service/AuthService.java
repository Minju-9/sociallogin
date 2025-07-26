package com.example.ssoproject.auth.service;

import com.example.ssoproject.domain.entity.User;
import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, facebook, kakao, naver
        String socialId = getSocialId(oAuth2User, provider);
        String name = getName(oAuth2User, provider);
        String email = getEmail(oAuth2User, provider);

        // ✅ 기존 유저 확인 후 없으면 신규 저장
        userRepository.findBySocialIdAndProvider(socialId, provider)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .provider(provider)
                                .name(name != null ? name : "Unknown")
                                .email(email != null ? email : null) // ✅ null 허용
                                .createdAt(LocalDateTime.now())
                                .build()
                ));


        return oAuth2User;
    }

    private String getSocialId(OAuth2User oAuth2User, String provider) {
        return switch (provider) {
            case "google" -> (String) oAuth2User.getAttributes().get("sub");
            case "facebook" -> (String) oAuth2User.getAttributes().get("id");
            case "kakao" -> String.valueOf(oAuth2User.getAttributes().get("id"));
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                yield (String) response.get("id");
            }
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private String getName(OAuth2User oAuth2User, String provider) {
        return switch (provider) {
            case "google" -> (String) oAuth2User.getAttributes().get("name");
            case "facebook" -> (String) oAuth2User.getAttributes().get("name");
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                yield (String) profile.get("nickname");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                yield (String) response.get("name");
            }
            default -> "Unknown";
        };
    }

    private String getEmail(OAuth2User oAuth2User, String provider) {
        return switch (provider) {
            case "google" -> (String) oAuth2User.getAttributes().get("email");
            case "facebook" -> (String) oAuth2User.getAttributes().get("email");
            case "kakao" -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
                yield (String) kakaoAccount.get("email");
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
                yield (String) response.get("email");
            }
            default -> null;
        };
    }
}
