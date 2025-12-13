package k23cnt1.nqt.project3.nqtController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) {
        // Check if response is already committed (e.g., redirect already sent)
        if (response.isCommitted()) {
            // Response already committed, can't render view
            return null;
        }
        
        // Check if this is an AJAX request (JSON expected)
        String requestURI = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        boolean isAjaxRequest = (acceptHeader != null && acceptHeader.contains("application/json")) ||
                               (requestURI != null && (requestURI.contains("/upload-banner") || 
                                                       requestURI.contains("/test-smtp") ||
                                                       requestURI.contains("/api/")));
        
        if (isAjaxRequest) {
            // Return JSON response for AJAX requests
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi không xác định");
            
            // Log error for debugging
            System.err.println("Error in AJAX request: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
        
        // Return HTML error page for regular requests
        model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Đã xảy ra lỗi không xác định");
        
        // Log stack trace for debugging (only in development)
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        model.addAttribute("trace", sw.toString());
        
        // Determine if this is a customer or admin request
        if (requestURI != null && (requestURI.startsWith("/admin") || requestURI.startsWith("/nqtAdmin"))) {
            // Admin error page
            return "error";
        } else {
            // Customer error page
            return "nqtCustomer/error";
        }
    }
}
