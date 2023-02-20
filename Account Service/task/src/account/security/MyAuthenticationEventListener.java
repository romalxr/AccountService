package account.security;

import account.service.EventService;
import account.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class MyAuthenticationEventListener implements
        ApplicationListener<AbstractAuthenticationEvent>
{
    private final HttpServletRequest request;
    private final UserService userService;
    private final EventService eventService;

    public MyAuthenticationEventListener(HttpServletRequest request, UserService userService, EventService eventService) {
        this.request = request;
        this.userService = userService;
        this.eventService = eventService;
    }
    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        String email = event.getAuthentication().getPrincipal().toString();
        if (event.getAuthentication().isAuthenticated()) {
            email = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        }

        if (event instanceof AuthenticationSuccessEvent) {
            userService.registerSuccessLogin(email);
        } else if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            eventService.addEventLoginFailed(email, request.getRequestURI());
            userService.registerBadLogin(email, request.getRequestURI());
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            userService.unlockWhenTimeExpired(email, request.getRequestURI());
        }
    }
}