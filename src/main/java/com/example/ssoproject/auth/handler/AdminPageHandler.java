package com.example.ssoproject.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminPageHandler implements AuthenticationSuccessHandler {

    private static final String LOCAL_URL = "http://localhost:5173/dashboard";
    private static final String PROD_URL = "https://sociallogin-fe.vercel.app/dashboard"; // ✅ 배포 프론트엔드 URL

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String referer = request.getHeader("referer");

        if (referer != null && referer.contains("sociallogin-fe.vercel.app")) {
            response.sendRedirect(PROD_URL);
        } else {
            response.sendRedirect(LOCAL_URL);
        }
    }
}
