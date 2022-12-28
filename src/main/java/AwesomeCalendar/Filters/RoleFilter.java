package AwesomeCalendar.Filters;

import AwesomeCalendar.Entities.Event;
import AwesomeCalendar.Entities.Role;
import AwesomeCalendar.Entities.User;
import AwesomeCalendar.Repositories.EventRepo;
import AwesomeCalendar.Services.EventService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoleFilter implements Filter {
    private final EventRepo eventRepo;

    private final EventService eventService;

    private static final Logger logger = LogManager.getLogger(RoleFilter.class);

    private static Map<String, List<String>> destinationsWithPermissions;

    public RoleFilter(EventRepo eventRepo, EventService eventService) {
        this.eventRepo = eventRepo;
        this.eventService = eventService;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("endpoints.json");
        Reader reader = new InputStreamReader(resourceAsStream)) {
            Gson gson = new Gson();
            Type MapType = new TypeToken<Map<String, List<String>>>() {}.getType();
            assert resourceAsStream != null;
            destinationsWithPermissions = gson.fromJson(reader, MapType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("Applying Role filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();
        User user = (User) req.getAttribute("user");
        String eventId = req.getParameter("eventId");

        if (destinationsWithPermissions.get("ORGANIZER").contains(path)) {
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
        } else if (destinationsWithPermissions.get("NOT_GUEST").contains(path)) {
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
