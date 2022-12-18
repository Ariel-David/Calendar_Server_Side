package AwesomeCalendar.Filters;

import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Filters.FilterObjects.MutableHttpServletRequest;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Utility;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter implements Filter {
    private final AuthService authService;

    public TokenFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MutableHttpServletRequest req = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();

        if (!Utility.destinations.contains(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        String token = req.getHeader("token");
        try {
            User user = authService.checkToken(token);
            Gson gson = new Gson();
            req.setAttribute("theUser", user);
            filterChain.doFilter(req, res);
            //servletRequest.setAttribute("user", gson.toJson(user));
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
//        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
