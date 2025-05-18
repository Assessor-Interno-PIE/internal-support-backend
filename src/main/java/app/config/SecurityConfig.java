package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SecurityConfig class configures security settings for the application,
 * enabling security filters and setting up OAuth2 login and logout behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
			if (resourceAccess != null && resourceAccess.containsKey("internal-support")) {
				Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("internal-support");
				if (clientAccess != null && clientAccess.containsKey("roles")) {
					List<String> roles = (List<String>) clientAccess.get("roles");
					return roles.stream()
							.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
							.collect(Collectors.toList());
				}
			}
			return List.of();
		});
		return converter;
	}

	/**
	 * Configures the security filter chain for handling HTTP requests, OAuth2 login, and logout.
	 *
	 * @param http HttpSecurity object to define web-based security at the HTTP level
	 * @param jwtDecoder the JWT decoder bean
	 * @return SecurityFilterChain for filtering and securing HTTP requests
	 * @throws Exception in case of an error during configuration
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
				.cors(Customizer.withDefaults()) // Disable CORS for API endpoints
				// Configures authorization rules for different endpoints
				.authorizeHttpRequests(authorize -> authorize
						// Swagger UI v3 (OpenAPI)
						.requestMatchers("/v3/api-docs/**").permitAll()
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/swagger-ui.html").permitAll()
						// Endpoints públicos
						.requestMatchers("/").permitAll() // Allows public access to the root URL
						.requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

						// TESTING ENDPOINTS
						.requestMatchers("/api/auth/userinfo").permitAll() // PARA O SWAGGER
						.requestMatchers("/api/auth/check").permitAll()
						.requestMatchers("/oauth2/**").permitAll()
						.requestMatchers("/login/**").permitAll()
						.requestMatchers("/api/auth/login").permitAll()

						// Keycloak Groups
						.requestMatchers("/api/keycloak/groups/**").hasAuthority("ROLE_ADMIN")

						// Keycloak Users
						.requestMatchers("/api/keycloak/users/**").hasAuthority("ROLE_ADMIN")

						// Documents
						.requestMatchers("/api/documents/by-department/**").authenticated()
						.requestMatchers("/api/documents/download/**").authenticated()
						.requestMatchers("/api/documents/paginated/**").authenticated()
						.requestMatchers("/api/documents/view/**").authenticated()
						.requestMatchers("/api/documents/**").hasAuthority("ROLE_ADMIN")
						.anyRequest().authenticated()
				)
				.oauth2Login(Customizer.withDefaults())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt
								.decoder(jwtDecoder)
								.jwtAuthenticationConverter(jwtAuthenticationConverter())
						)
				);

		return http.build();
	}
}