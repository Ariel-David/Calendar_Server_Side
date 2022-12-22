package AwesomeCalendar.Filters;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Services.EventService;
import AwesomeCalendar.Utilities.Utility;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RoleFilter implements Filter {
    private final EventRepo eventRepo;

    private final EventService eventService;

    public RoleFilter(EventRepo eventRepo, EventService eventService) {
        this.eventRepo = eventRepo;
        this.eventService = eventService;
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
            Role role = eventService.getRoleByEventAndUSer(event.get().getId(), user);
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
            Role role = eventService.getRoleByEventAndUSer(event.get().getId(), user);
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
