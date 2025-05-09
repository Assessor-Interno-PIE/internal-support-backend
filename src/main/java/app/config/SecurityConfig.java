package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


/**
 * SecurityConfig class configures security settings for the application,
 * enabling security filters and setting up OAuth2 login and logout behavior.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Configures the security filter chain for handling HTTP requests, OAuth2 login, and logout.
	 *
	 * @param http HttpSecurity object to define web-based security at the HTTP level
	 * @return SecurityFilterChain for filtering and securing HTTP requests
	 * @throws Exception in case of an error during configuration
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API endpoints
				.cors(AbstractHttpConfigurer::disable) // Disable CORS for API endpoints
				// Configures authorization rules for different endpoints
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/").permitAll() // Allows public access to the root URL
						.requestMatchers("/menu").authenticated() // Requires authentication to access "/menu"
						// Document endpoints security
						.requestMatchers("/api/documents/view/**").authenticated() // View documents requires authentication
						.requestMatchers("/api/documents/download/**").authenticated() // Download requires authentication
						.requestMatchers("/api/documents/find-by-id/**").authenticated() // Find by ID requires authentication
						.requestMatchers("/api/documents/find-all").authenticated() // List all requires authentication
						.requestMatchers("/api/documents/find-all/paginated").authenticated() // Paginated list requires authentication
						.requestMatchers("/api/documents/by-department/**").authenticated() // Department filter requires authentication
						.requestMatchers("/api/documents/search/title-contains").authenticated() // Search requires authentication
						.requestMatchers("/api/documents/save").authenticated() // Save requires authentication
						.requestMatchers("/api/documents/edit/**").authenticated() // Edit requires authentication
						.requestMatchers("/api/documents/**").authenticated() // Any other document endpoint requires authentication
						.anyRequest().authenticated() // Requires authentication for any other request
				)
				// Configures OAuth2 login settings
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/oauth2/authorization/keycloak") // Sets custom login page for OAuth2 with Keycloak
						.defaultSuccessUrl("/menu", true) // Redirects to "/menu" after successful login
				)
				// Configures logout settings
				.logout(logout -> logout
						.logoutSuccessUrl("/") // Redirects to the root URL on successful logout
						.invalidateHttpSession(true) // Invalidates session to clear session data
						.clearAuthentication(true) // Clears authentication details
						.deleteCookies("JSESSIONID") // Deletes the session cookie
				);

		return http.build();
	}
}
