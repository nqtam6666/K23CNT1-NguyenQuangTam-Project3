package k23cnt1.nqt.project3.nqtConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NqtOAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        if (response.isCommitted()) {
            return;
        }
        
        try {
            String errorMessage = exception.getMessage();
            String encodedMessage = errorMessage != null ? java.net.URLEncoder.encode(errorMessage, "UTF-8") : "";
            getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=oauth2_failed&message=" + encodedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                getRedirectStrategy().sendRedirect(request, response, "/nqtDangNhap?error=oauth2_failed");
            }
        }
    }
}

