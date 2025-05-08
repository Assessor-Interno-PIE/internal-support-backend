package app.config;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public KeycloakConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		ClientRegistration clientRegistration = ClientRegistration
				.withRegistrationId("keycloak")
				.clientId("internal_support")
				.clientSecret("senha123")
				.authorizationUri("http://localhost:9090/realms/internal_support/protocol/openid-connect/auth")
				.tokenUri("http://localhost:9090/realms/internal_support/protocol/openid-connect/token")
				.jwkSetUri("http://localhost:9090/realms/internal_support/protocol/openid-connect/certs")
				.userInfoUri("http://localhost:9090/realms/internal_support/protocol/openid-connect/userinfo")
				.redirectUri("http://localhost:9090/login/oauth2/code/keycloak")
				.clientName("Keycloak")
				.scope("openid", "profile", "email")
				.authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
				.build();

		return new InMemoryClientRegistrationRepository(clientRegistration);
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
		return new InMemoryOAuth2AuthorizedClientService(repo);
	}

	@Bean
	public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService service) {
		return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(service);
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authBuilder.authenticationProvider(keycloakAuthenticationProvider());
		return authBuilder.build();
	}

	@Bean
	public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
		return new KeycloakAuthenticationProvider();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.requestMatchers("/api/public/**").permitAll()
				.requestMatchers("/api/**").hasRole("USER")
				.anyRequest().authenticated()
				.and()
				.oauth2Login()
				.permitAll();

		return http.build();
	}
}
