package mkoutra.birthdaykeeper.security;

import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.security.authentication.JwtAuthenticationFilter;
import mkoutra.birthdaykeeper.security.handlers.CustomAccessDeniedHandler;
import mkoutra.birthdaykeeper.security.handlers.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * A configuration class responsible for setting security filter chain,
 * specifying authentication beans like the AuthenticationManager, AuthenticationProvider etc.
 * Useful: <a href="https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-exceptiontranslationfilter">link</a>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final static int PASSWORD_ENCODER_WORKLOAD = 12;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;            // Finds our CustomUserDetailsService.

    // Configure the filters applied to each request
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: Add authorization filter
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))  // Configure CORS
                .csrf(AbstractHttpConfigurer::disable)                                                                  // Disable CSRF. We use JWT, and we don't use session based security
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint()))         // Handle authentication exception
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler(customAccessDeniedHandler()))                   // Handle authorization exception
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless because we use jwt
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);                  // Insert jwt filter before authentication

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = getCorsConfiguration();

        // Configure the endpoints on which the cors will be applied
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // applies the CORS settings to all endpoints in the application.
        return source;
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));  // Angular front-end
        corsConfiguration.setAllowedMethods(List.of("*"));  // allows all HTTP methods (e.g., GET, POST, PUT, DELETE, etc.).
        corsConfiguration.setAllowedHeaders(List.of("*"));  // allows all headers to be sent in requests (e.g., Authorization, Content-Type, Accept).
        corsConfiguration.setAllowCredentials(true);            // allows credentials (e.g., cookies, authorization headers) to be sent in cross-origin requests.
                                                                // This is critical when working with secure endpoints that require JWT tokens or session cookies.
        return corsConfiguration;
    }

    // Get the default ProviderManager implementation of AuthenticationManager interface
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Create the only authentication provider used in this app.
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    // Password encoder used for encryption/description of passwords.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(PASSWORD_ENCODER_WORKLOAD);
    }

    // AuthenticationEntryPoint handles authentication failure.
    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    // AccessDeniedHandler handles authorization failure.
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}