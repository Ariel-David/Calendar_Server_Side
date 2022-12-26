package AwesomeCalendar.Filters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CorsFilter implements Filter {
    private final Set<String> origins = new HashSet<>(Set.of("http://localhost:9000"));

    private static final Logger logger = LogManager.getLogger(CorsFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("Applying cors filter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse res=((HttpServletResponse)servletResponse);
        String header = request.getHeader("origin");
        if(origins.contains(header)){
            res.addHeader("Access-Control-Allow-Origin", header);
            res.addHeader("Access-Control-Allow-Headers", "*");
            res.addHeader("Access-Control-Allow-Methods",
                    "GET, OPTIONS, HEAD, PUT, POST, DELETE, PATCH");
            res.addHeader("Access-Control-Allow-Credentials", "true");

            if (request.getMethod().equals("OPTIONS")) {
                res.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }
        filterChain.doFilter(servletRequest, res);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
