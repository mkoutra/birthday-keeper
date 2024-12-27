package mkoutra.birthdaykeeper.security.handlers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * Class handling the case that logout was successful by sending a JSON.
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        String json = "{\"code\": \"LogoutSuccess\", " +
                "\"description\": \"Logout was successful.\"}";

        response.setStatus(HttpServletResponse.SC_OK);  // 200
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        LOGGER.info("Successful logout");
    }
}
