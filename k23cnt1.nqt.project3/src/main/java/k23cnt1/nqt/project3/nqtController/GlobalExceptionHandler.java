package k23cnt1.nqt.project3.nqtController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        // Check if response is already committed (e.g., redirect already sent)
        if (response.isCommitted()) {
            // Response already committed, can't render view
            return null;
        }
        
        model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi không xác định");
        
        // Log stack trace for debugging (only in development)
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        model.addAttribute("trace", sw.toString());
        
        // Determine if this is a customer or admin request
        String requestURI = request.getRequestURI();
        if (requestURI != null && (requestURI.startsWith("/admin") || requestURI.startsWith("/nqtAdmin"))) {
            // Admin error page
            return "error";
        } else {
            // Customer error page
            return "nqtCustomer/error";
        }
    }
}
