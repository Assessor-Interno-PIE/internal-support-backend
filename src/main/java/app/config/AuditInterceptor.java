package app.config;

import app.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Autowired
    private AuditService auditService;

    @Autowired
    private JwtDecoder jwtDecoder;

    // Endpoints que devemos ignorar para auditoria
    private static final List<String> IGNORED_ENDPOINTS = Arrays.asList(
            "/api/logs",                 // Não auditar consultas de logs
            "/api/auth/validate",        // Validações de token
            "/api/auth/refresh",         // Refresh de token
            "/api/health",               // Health checks
            "/api/status",               // Status checks
            "/actuator",                 // Actuator endpoints
            "/favicon.ico",              // Favicon
            "/static/",                  // Arquivos estáticos
            "/css/",                     // CSS
            "/js/",                      // JavaScript
            "/images/"                   // Imagens
    );

    // Apenas métodos importantes para auditoria
    private static final List<String> AUDITED_METHODS = Arrays.asList(
            "POST", "PUT", "DELETE", "PATCH", "GET"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        
        // Verifica se deve auditar esta requisição
        if (shouldAuditRequest(endpoint, method)) {
            try {
                String userId = extractUserId(request);
                String ipAddress = getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");
                String action = method + " " + endpoint;
                
                // Determina a ação baseada no método HTTP
                String auditAction = getAuditAction(method, endpoint);
                
                // Salva o log de auditoria
                auditService.logHttpOperation(endpoint, method, auditAction, userId, ipAddress, userAgent);
                
            } catch (Exception e) {
                System.err.println("Erro ao processar auditoria: " + e.getMessage());
            }
        }
    }

    private String getAuditAction(String method, String endpoint) {
        switch (method) {
            case "POST":
                return "CREATE";
            case "PUT":
                return "UPDATE";
            case "DELETE":
                return "DELETE";
            case "GET":
                return "READ";
            case "PATCH":
                return "UPDATE";
            default:
                return method;
        }
    }

    private boolean shouldAuditRequest(String endpoint, String method) {
        // Ignora endpoints específicos
        for (String ignoredEndpoint : IGNORED_ENDPOINTS) {
            if (endpoint.startsWith(ignoredEndpoint)) {
                return false;
            }
        }

        // Só audita métodos importantes (POST, PUT, DELETE, PATCH)
        if (!AUDITED_METHODS.contains(method)) {
            return false;
        }

        // Audita endpoints de API importantes
        if (endpoint.startsWith("/api/")) {
            return true;
        }

        // Audita endpoints de documentos
        if (endpoint.startsWith("/documents/")) {
            return true;
        }

        return false;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty()) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                Jwt jwt = jwtDecoder.decode(token);

                // Busca o usuário nos claims do JWT - ordem de prioridade
                String userId = jwt.getClaimAsString("preferred_username");
                if (userId == null || userId.trim().isEmpty()) {
                    userId = jwt.getClaimAsString("email");
                }
                if (userId == null || userId.trim().isEmpty()) {
                    userId = jwt.getClaimAsString("name");
                }
                if (userId == null || userId.trim().isEmpty()) {
                    userId = jwt.getClaimAsString("sub"); // subject como último recurso
                }

                // Limpa e valida o userId
                if (userId != null) {
                    userId = userId.trim();
                    // Verifica se não é um valor inválido comum
                    if (!userId.isEmpty() && !userId.equals("null") && !userId.equals("undefined")) {
                        return userId;
                    }
                }

                return "unknown_user";

            } catch (Exception e) {
                System.err.println("Erro ao decodificar JWT: " + e.getMessage());
                return "token_error";
            }
        }
        return "anonymous";
    }
}