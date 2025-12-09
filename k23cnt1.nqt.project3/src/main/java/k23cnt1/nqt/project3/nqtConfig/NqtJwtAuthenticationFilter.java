package k23cnt1.nqt.project3.nqtConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import k23cnt1.nqt.project3.nqtEntity.NqtNguoiDung;
import k23cnt1.nqt.project3.nqtRepository.NqtNguoiDungRepository;
import k23cnt1.nqt.project3.nqtService.NqtJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class NqtJwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private NqtJwtService jwtService;

    @Autowired
    private NqtNguoiDungRepository nqtNguoiDungRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            final String authHeader = request.getHeader("Authorization");
            String jwt = null;
            String username = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                try {
                    username = jwtService.extractUsername(jwt);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        if (jwtService.validateToken(jwt, username)) {
                            setAuthentication(username, request);
                        }
                    }
                } catch (Exception e) {
                    // Invalid token, continue without authentication
                }
            } else {
                // Also check for JWT in cookie
                jakarta.servlet.http.Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (jakarta.servlet.http.Cookie cookie : cookies) {
                        if ("jwt".equals(cookie.getName())) {
                            jwt = cookie.getValue();
                            if (jwt != null) {
                                try {
                                    if (jwtService.validateToken(jwt)) {
                                        username = jwtService.extractUsername(jwt);
                                        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                            setAuthentication(username, request);
                                        }
                                    }
                                } catch (Exception e) {
                                    // Invalid token, continue without authentication
                                }
                            }
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Log but don't break the filter chain
            // Continue to next filter even if JWT processing fails
        }
        
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String username, HttpServletRequest request) {
        Optional<NqtNguoiDung> userOptional = nqtNguoiDungRepository.findByNqtTaiKhoanOrNqtEmail(username, username);
        
        if (userOptional.isPresent()) {
            NqtNguoiDung user = userOptional.get();
            String role = "ROLE_USER";
            if (user.getNqtVaiTro() != null) {
                if (user.getNqtVaiTro() == 99) {
                    role = "ROLE_ADMIN";
                } else if (user.getNqtVaiTro() == 1) {
                    role = "ROLE_EMPLOYEE";
                }
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            // Also set session for backward compatibility
            HttpSession session = request.getSession(true);
            session.setAttribute("nqtCustomerUser", user);
            session.setAttribute("nqtCustomerSession", user.getNqtTaiKhoan());
            
            if (user.getNqtVaiTro() != null && (user.getNqtVaiTro() == 99 || user.getNqtVaiTro() == 1)) {
                session.setAttribute("nqtAdminUser", user);
                session.setAttribute("nqtAdminSession", user.getNqtTaiKhoan());
            }
        }
    }
}

