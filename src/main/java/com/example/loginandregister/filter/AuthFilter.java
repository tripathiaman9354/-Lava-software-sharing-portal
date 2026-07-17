package com.example.loginandregister.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 1) Blocks any request to a protected page unless a valid session exists
 *    (fixes: dashboard opening after Sign out via Back button / direct URL).
 * 2) Sends no-cache headers on every response so the browser cannot serve
 *    a stale copy of a protected page from its cache.
 */
@Component
public class AuthFilter implements Filter {

    // Paths that DON'T require login
    private static final String[] PUBLIC_PATHS = {
            "/login",
            "/forgot-password",
            "/verify-otp",
            "/resend-otp",
            "/reset-password",
            "/css",
            "/js",
            "/images"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getRequestURI();

        // Always disable caching so Back/Forward never shows a stale page
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (isPublicPath(uri)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedinUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (uri.startsWith("/admin-dashboard")
                || uri.startsWith("/dashboard")
                || uri.startsWith("/odm/create")) {
            String role = (String) session.getAttribute("role");
            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        if (uri.startsWith("/user-dashboard") || uri.startsWith("/odmdashboard")) {
            String role = (String) session.getAttribute("role");
            if (!"USER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private boolean isPublicPath(String uri) {
        for (String path : PUBLIC_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
