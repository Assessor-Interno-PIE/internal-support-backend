package app.auth.service;

import app.exception.handler.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável por lidar com autenticação via Keycloak
 * e fornecer dados do usuário autenticado.
 */
@Service
public class AuthService {

	private final RestTemplate restTemplate;

	@Value("${keycloak.auth-server-url}")
	private String keycloakUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@Value("${keycloak.client-id}")
	private String clientId;

	public AuthService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Envia as credenciais do usuário para o Keycloak e recupera o token.
	 *
	 * @param username nome de login do usuário
	 * @param password senha correspondente
	 * @return mapa com o token e informações associadas
	 */
	public Map<String, Object> login(String username, String password) {
		String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
			return response.getBody();
		} catch (Exception e) {
			throw new AuthenticationException("Não foi possível autenticar o usuário.");
		}
	}

	/**
	 * Retorna os dados principais do usuário com base no token JWT.
	 *
	 * @param authentication autenticação ativa no contexto atual
	 * @return mapa com dados como id, nome, email, permissões e departamentos
	 */
	public Map<String, Object> getUserInfo(Authentication authentication) {
		Map<String, Object> userInfo = new HashMap<>();

		if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
			userInfo.put("id", jwt.getSubject());
			userInfo.put("username", jwt.getClaim("preferred_username"));
			userInfo.put("name", jwt.getClaim("name"));
			userInfo.put("email", jwt.getClaim("email"));

			List<String> roles = authentication.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			userInfo.put("roles", roles);

			List<String> groups = jwt.getClaim("groups");
			if (groups != null) {
				List<String> departments = groups.stream()
						.filter(group -> group.startsWith("DEPT_"))
						.map(group -> group.replace("DEPT_", ""))
						.collect(Collectors.toList());
				userInfo.put("departments", departments);

				String currentDepartment = departments.isEmpty() ? null : departments.get(0);
				userInfo.put("currentDepartment", currentDepartment);
			}

			userInfo.put("isAuthenticated", authentication.isAuthenticated());
		}

		return userInfo;
	}

	/**
	 * Verifica se o usuário está logado.
	 *
	 * @param authentication autenticação fornecida pelo Spring Security
	 * @return true se a sessão estiver ativa, caso contrário false
	 */
	public boolean isAuthenticated(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated();
	}
}
