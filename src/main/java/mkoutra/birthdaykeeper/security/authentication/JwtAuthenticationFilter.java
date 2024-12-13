package mkoutra.birthdaykeeper.security.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // about OncePerRequestFilter: https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-exceptiontranslationfilter

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;    // Our CustomUserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // Read authorization header from request
        String jwtToken;
        String usernameFromToken;

        // No authorization header found, or it does not contain a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.warn("Invalid Authorization Header.");
            filterChain.doFilter(request, response);            // Move to the next filter
            return;
        }

        jwtToken = authHeader.substring("Bearer ".length());     // Extract the jwt from the header

        try {
            usernameFromToken = jwtService.extractSubject(jwtToken); // Extract username from jwt

            // If the user has not been authenticated before (i.e. The authentication object inside the security context is null)
            if ((usernameFromToken != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken); // Get user

                // If the jwt is valid insert authentication object inside the SecurityContext
                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(    // Create the Authentication object
                            userDetails,
                            null,   // Password is not needed since JWT already verified the user
                            userDetails.getAuthorities()
                    );

                    // Attaches additional request details (e.g., IP address, session ID) to
                    // the authentication object using WebAuthenticationDetailsSource.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Based on https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-securitycontextholder
                    SecurityContext context = SecurityContextHolder.createEmptyContext();   // Create empty SecurityContext
                    context.setAuthentication(authToken);                                   // Insert the authentication object inside the securityContext
                    SecurityContextHolder.setContext(context);                              // Insert SecurityContext inside SecurityContextHolder

                    // SecurityContextHolder.getContext().setAuthentication(authToken);     // Alternative approach

                    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    // User loggedInUser = authentication == null ? null : (User) authentication.getPrincipal();
                    // LOGGER.info("Logged in user: {} with role {}", loggedInUser.getUsername(), loggedInUser.getRole());
                } else {
                    LOGGER.warn("Token is invalid.");
                }
            }
        } catch (ExpiredJwtException e) {
            LOGGER.warn("WARN: Expired token ", e);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            String jsonBody = "{\"code\": \"expiredToken\", \"description\": \"" + e.getMessage() + "\"}";
            response.getWriter().write(jsonBody);
            return;
        } catch (Exception e) {
            LOGGER.warn("WARN: Something went wrong while parsing JWT ", e);

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            String jsonBody = "{\"code\": \"invalidToken\", \"description\": \"" + e.getMessage() + "\"}";
            response.getWriter().write(jsonBody);
            return;
        }

        filterChain.doFilter(request, response);    // Move to the next filter of the SecurityFilterChain
    }
}
