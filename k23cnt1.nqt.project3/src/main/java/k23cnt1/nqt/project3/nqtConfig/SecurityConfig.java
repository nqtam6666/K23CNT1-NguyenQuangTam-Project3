package k23cnt1.nqt.project3.nqtConfig;

import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private NqtJwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private NqtOAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private NqtOAuth2FailureHandler oAuth2FailureHandler;

    @Autowired
    private NqtAdminPathService adminPathService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String adminPath = adminPathService.getAdminPathWithSlash();
        String adminLoginPath = adminPath + "/login";
        
        http
            .csrf(csrf -> csrf.disable())
            // Use IF_REQUIRED instead of STATELESS to support both JWT and session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints
                .requestMatchers("/", "/nqt**", "/static/**", "/uploads/**", "/favicon.ico").permitAll()
                // Block access to /admin/** if admin path has been changed
                .requestMatchers(request -> {
                    String path = request.getRequestURI();
                    // If admin path is not default and request is to /admin/**, deny access
                    if (!adminPath.equals("/admin") && path.startsWith("/admin/")) {
                        // Allow static resources from /admin/ (css, js, images) for backward compatibility
                        return path.startsWith("/admin/css/") || 
                               path.startsWith("/admin/js/") || 
                               path.startsWith("/admin/images/");
                    }
                    return false;
                }).denyAll()
                // Admin login and static resources - permit all (using dynamic path)
                .requestMatchers(request -> {
                    String path = request.getRequestURI();
                    return path.equals(adminLoginPath) || 
                           path.startsWith(adminPath + "/css/") ||
                           path.startsWith(adminPath + "/js/") ||
                           path.startsWith(adminPath + "/images/");
                }).permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                // Admin endpoints - require authentication (using dynamic path)
                .requestMatchers(request -> {
                    String path = request.getRequestURI();
                    return path.startsWith(adminPath + "/") && !path.equals(adminLoginPath) &&
                           !path.startsWith(adminPath + "/css/") &&
                           !path.startsWith(adminPath + "/js/") &&
                           !path.startsWith(adminPath + "/images/");
                }).authenticated()
                // API endpoints - can be protected later
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/nqtDangNhap")
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
