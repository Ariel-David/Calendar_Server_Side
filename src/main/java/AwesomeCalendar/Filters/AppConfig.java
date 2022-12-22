package AwesomeCalendar.Filters;

import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final AuthService authService;
    private final EventRepo eventRepo;
    private final EventService eventService;

    @Autowired
    public AppConfig(AuthService authService, EventRepo eventRepo, EventService eventService) {
        System.out.println("AppConfig is created");
        this.authService = authService;
        this.eventRepo = eventRepo;
        this.eventService = eventService;
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
        RoleFilter roleFilter = new RoleFilter(eventRepo, eventService);
        roleBean.setFilter(roleFilter);
        roleBean.addUrlPatterns("/*");
        roleBean.setOrder(3);
        return roleBean;
    }
}
