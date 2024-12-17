package mkoutra.birthdaykeeper.security.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * This class will be used if the authentication fails.
 * It is used in the SecurityFilterChain inside the SecurityConfiguration class.
 * This is a handler that handles failed authentication.
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        String json = "{" +
                        "\"code\": \"authenticationException\", " +
                        "\"description\": \"" + authException.getMessage() + "\" " +
                      "}";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401 Unauthorized
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
