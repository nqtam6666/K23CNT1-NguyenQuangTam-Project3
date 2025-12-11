package k23cnt1.nqt.project3.nqtConfig;

import k23cnt1.nqt.project3.nqtService.NqtAdminPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;
    
    @Autowired
    private NqtAdminPathService adminPathService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Use dynamic admin path
        String adminPath = adminPathService.getAdminPathWithSlash();
        String adminLoginPath = adminPath + "/login";
        String adminCssPattern = adminPath + "/css/**";
        String adminJsPattern = adminPath + "/js/**";
        String adminImagesPattern = adminPath + "/images/**";
        String adminAllPattern = adminPath + "/**";
        
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns(adminAllPattern)
                .excludePathPatterns(adminLoginPath, adminCssPattern, adminJsPattern, adminImagesPattern);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }
}
