package app.config;

import app.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditService auditService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String ip = getClientIpAddress(request);
        String userId = extractUserId(request);

        if (endpoint.startsWith("/api/") || endpoint.startsWith("/documents/")) {
            auditService.logHttpRequest(endpoint, method, userAgent, ip, userId);
        }

        return true;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Se você tem um serviço para decodificar JWT
                // return jwtService.getUserIdFromToken(authHeader.substring(7));

                // Alternativa: pegar do SecurityContext se configurado
                // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                // if (auth != null && auth.isAuthenticated()) {
                //     return auth.getName();
                // }

                return "authenticated_user";
            } catch (Exception e) {
                return "token_error";
            }
        }
        return "anonymous";
    }
}