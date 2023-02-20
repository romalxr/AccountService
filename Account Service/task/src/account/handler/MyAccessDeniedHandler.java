package account.handler;

import account.service.EventService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class MyAccessDeniedHandler implements AccessDeniedHandler {

    EventService eventService;
    public MyAccessDeniedHandler(EventService eventService) {
        this.eventService = eventService;
    }
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        String path = request.getRequestURI();
        eventService.addEventAccessDenied(username, path);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
    }
}
