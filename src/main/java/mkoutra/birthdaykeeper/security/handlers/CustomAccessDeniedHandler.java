package mkoutra.birthdaykeeper.security.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * If it is an AccessDeniedException, then Access Denied.
 * The AccessDeniedHandler is invoked to handle access denied.
 * It is used in the SecurityFilterChain inside the SecurityConfiguration class.
 * This is a handler used when there is an issue with authorization.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);   // 403 Forbidden
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = "{" +
                          "\"code\": \"AccessDenied\", " +
                          "\"description\": \"" + accessDeniedException.getMessage() + "\"" +
                      "}";
        response.getWriter().write(json);
    }
}
