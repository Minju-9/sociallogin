package com.example.ssoproject.auth.service;
import com.example.ssoproject.domain.entity.User;
import com.example.ssoproject.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String socialId = getSocialId(oAuth2User, provider);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        userRepository.findBySocialIdAndProvider(socialId, provider)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .provider(provider)
                                .email(email)
                                .name(name)
                                .build()
                ));

        return oAuth2User;
    }
    private String getSocialId(OAuth2User oAuth2User, String provider) {
        if(provider.equals("google")){
            return oAuth2User.getAttribute("sub");
        } else if(provider.equals("naver")){
            return ((Map<String, Object>) oAuth2User.getAttribute("response"))
                    .get("id").toString();
        } else if(provider.equals("kakao")){
            return  oAuth2User.getAttribute("id").toString();
        }
        throw new IllegalArgumentException("지원하지 않는 provider: " + provider);
    }
}

