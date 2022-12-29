package AwesomeCalendar.Filters;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Services.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TokenFilter implements Filter {
    private final AuthService authService;

    public static List<String> destinations = new ArrayList<>(List.of("/event/new", "/event/removeUser", "/event/update", "/event/new/role", "/event/update/role/type", "/event/getEvent", "/event/delete", "/event/getBetweenDates", "/event/update/role/status", "/event/getUsers",
            "/sharing/share", "/sharing/sharedWithMe", "/event/getCalendarsBetweenDates","/notifications/settings", "/notifications/upcoming", "/notifications/getNotificationsSettings"));

    private static final Logger logger = LogManager.getLogger(TokenFilter.class);

    public TokenFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("Applying Token filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();

        if (!destinations.contains(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = req.getHeader("token");
        try {
            User user = authService.checkToken(token);
            req.setAttribute("user", user);
            filterChain.doFilter(req, res);
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
