package AwesomeCalendar.Filters;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Repositories.RoleRepo;
import AwesomeCalendar.Services.AuthService;
import AwesomeCalendar.Utilities.Utility;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RoleFilter implements Filter {
    private final RoleRepo roleRepo;
    private final EventRepo eventRepo;

    public RoleFilter(RoleRepo roleRepo, EventRepo eventRepo) {
        this.roleRepo = roleRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();
        User user = (User) req.getAttribute("user");
        String eventId = req.getParameter("eventId");

        if (Utility.destinationsPermissionsOrganizer.contains(path)) {
            Optional<Event> event = eventRepo.findById(Long.parseLong(eventId));
            if (!event.isPresent()) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Role role = roleRepo.findByEventAndUser(event.get(), user);
            if (role.getRoleType().equals(Role.RoleType.ORGANIZER)) {
                filterChain.doFilter(req, res);
            } else {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else if (Utility.destinationsPermissionsNotGuest.contains(path)) {
            Optional<Event> event = eventRepo.findById(Long.parseLong(eventId));
            if (!event.isPresent()) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Role role = roleRepo.findByEventAndUser(event.get(), user);
            if (!role.getRoleType().equals(Role.RoleType.GUEST)) {
                if(path.equals("/event/update")){
                    req.setAttribute("userType", role.getRoleType());
                }
                filterChain.doFilter(req, res);
            } else {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else {
            filterChain.doFilter(req, res);
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
