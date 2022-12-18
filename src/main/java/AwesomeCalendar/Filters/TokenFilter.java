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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

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
        System.out.println(token);
        try {
            User user = authService.checkToken(token);
            Gson gson = new Gson();
            //req.putHeader("user", gson.toJson(user));
            servletRequest.setAttribute("user", gson.toJson(user));
            for (String name : Collections.list(servletRequest.getAttributeNames())) {
                System.out.println(name);
            }
        } catch (IllegalArgumentException e) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
