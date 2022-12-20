package AwesomeCalendar.Filters;

import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.RoleRepo;
import AwesomeCalendar.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final AuthService authService;
    private final RoleRepo roleRepo;
    private final EventRepo eventRepo;

    @Autowired
    public AppConfig(AuthService authService, RoleRepo roleRepo, EventRepo eventRepo) {
        System.out.println("AppConfig is created");
        this.authService = authService;
        this.roleRepo = roleRepo;
        this.eventRepo = eventRepo;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>();
        CorsFilter corsFilter = new CorsFilter();
        corsBean.setFilter(corsFilter);
        corsBean.addUrlPatterns("/*");
        corsBean.setOrder(1);
        return corsBean;
    }

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterBean() {
        FilterRegistrationBean<TokenFilter> tokenBean = new FilterRegistrationBean<>();
        TokenFilter tokenFilter = new TokenFilter(authService);
        tokenBean.setFilter(tokenFilter);
        tokenBean.addUrlPatterns("/*");
        tokenBean.setOrder(2);
        return tokenBean;
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterBean() {
        FilterRegistrationBean<RoleFilter> roleBean = new FilterRegistrationBean<>();
        RoleFilter roleFilter = new RoleFilter(roleRepo, eventRepo);
        roleBean.setFilter(roleFilter);
        roleBean.addUrlPatterns("/*");
        roleBean.setOrder(3);
        return roleBean;
    }
}
