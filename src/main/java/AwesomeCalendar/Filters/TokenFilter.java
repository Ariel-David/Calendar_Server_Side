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
/**

 TokenFilter is a class that implements the Filter interface and is used to authenticate requests to certain destinations in a web application.

 @author [Your Name]

 @implNote The filter uses an instance of AuthService to check the validity of a token provided in the request header. If the token is valid, the filter

 allows the request to proceed and sets the user attribute in the request with the corresponding User object. If the token is invalid, the filter

 returns a 403 FORBIDDEN response.

 The destinations that require authentication are specified in the destinations field.

 The class also has a logger to log messages at different levels of severity.
 */
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

    /**
     Filters requests to certain destinations by checking the validity of a token provided in the request header.
     @param servletRequest the servlet request
     @param servletResponse the servlet response
     @param filterChain the filter chain
     @throws IOException if an I/O error occurs
     @throws ServletException if a servlet error occurs
     @implNote The method first gets the request URI and checks if the request is to a destination that requires authentication. If not, the request is allowed
     to proceed without further checks. If the request is to a destination that requires authentication, the method gets the token from the request header
     and uses the AuthService to check its validity. If the token is valid, the method sets the user attribute in the request with the corresponding
     User object and allows the request to proceed. If the token is invalid, the method returns a 403 FORBIDDEN response.
     */
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
