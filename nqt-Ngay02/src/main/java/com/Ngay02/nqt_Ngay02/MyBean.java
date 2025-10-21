package com.Ngay02.nqt_Ngay02;
import com.Ngay02.nqt_Ngay02.pkg_annotation.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class MyBean {
    @Autowired
    private final AppConfig appConfig;
    public MyBean(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    @GetMapping("/my-bean")
    public String myBean() {
        return appConfig.appName();
    }
}