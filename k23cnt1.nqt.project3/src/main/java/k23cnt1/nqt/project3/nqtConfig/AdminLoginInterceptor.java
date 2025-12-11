package k23cnt1.nqt.project3.nqtConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
    
    @Autowired
    private NqtAdminPathService adminPathService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // Don't create session if not exists
        
        // First check SecurityContext (from JWT)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof NqtNguoiDung) {
            NqtNguoiDung user = (NqtNguoiDung) auth.getPrincipal();
            if (user.getNqtVaiTro() != null && (user.getNqtVaiTro() == 99 || user.getNqtVaiTro() == 1)) {
                // Sync to session for backward compatibility
                if (session != null) {
                    session.setAttribute("nqtAdminUser", user);
                    session.setAttribute("nqtAdminSession", user.getNqtTaiKhoan());
                    session.setAttribute("nqtCustomerUser", user);
                    session.setAttribute("nqtCustomerSession", user.getNqtTaiKhoan());
                }
                return true;
            }
        }
        
        // Fallback to session check
        if (session != null) {
            // Kiểm tra admin session và quyền
            NqtNguoiDung adminUser = (NqtNguoiDung) session.getAttribute("nqtAdminUser");
            if (adminUser != null && adminUser.getNqtVaiTro() != null) {
                // Kiểm tra quyền: chỉ admin (99) hoặc nhân viên (1) mới được truy cập
                if (adminUser.getNqtVaiTro() == 99 || adminUser.getNqtVaiTro() == 1) {
                    return true;
                } else {
                    // User không có quyền, xóa session và redirect
                    session.invalidate();
                    String adminLoginPath = adminPathService.getAdminLoginPath();
                    response.sendRedirect(adminLoginPath + "?error=noPermission");
                    return false;
                }
            }
            
            // Nếu không có admin session, kiểm tra customer session có phải admin/nhân viên không
            NqtNguoiDung customerUser = (NqtNguoiDung) session.getAttribute("nqtCustomerUser");
            if (customerUser != null && customerUser.getNqtVaiTro() != null 
                    && (customerUser.getNqtVaiTro() == 99 || customerUser.getNqtVaiTro() == 1)) {
                // Tự động set admin session từ customer session
                session.setAttribute("nqtAdminSession", customerUser.getNqtTaiKhoan());
                session.setAttribute("nqtAdminUser", customerUser);
                return true;
            }
        }
        
        // Nếu không có session nào hợp lệ, redirect về login
        String adminLoginPath = adminPathService.getAdminLoginPath();
        response.sendRedirect(adminLoginPath);
        return false;
    }
}
