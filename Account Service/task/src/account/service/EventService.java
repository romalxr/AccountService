package account.service;

import account.dto.EventDTO;
import account.entity.Event;
import account.entity.Operation;
import account.mapper.EventMapper;
import account.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public List<EventDTO> findAll() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        return events.stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void addEventCreateUser(UserDetails userDetails, String object, String path) {
        String subject = getSubject(userDetails);
        Event event = create(
                "CREATE_USER",
                subject,
                object,
                path);
        eventRepository.save(event);
    }

    public void addEventDeleteUser(UserDetails userDetails, String object, String path) {
        String subject = getSubject(userDetails);
        Event event = create(
                "DELETE_USER",
                subject,
                object,
                path);
        eventRepository.save(event);
    }

    public void addEventChangePassword(String email, String path) {
        Event event = create(
                "CHANGE_PASSWORD",
                email,
                email,
                path);
        eventRepository.save(event);
    }

    public void addEventChangeRole(UserDetails userDetails, String operation, String role, String username, String path) {
        String subject = getSubject(userDetails);
        String action = "";
        String object = "";
        if (Operation.valueOf(operation) == Operation.GRANT) {
            action = "GRANT_ROLE";
            object = "Grant role "+role+" to "+username;
        } else if (Operation.valueOf(operation) == Operation.REMOVE) {
            action = "REMOVE_ROLE";
            object = "Remove role "+role+" from "+username;
        }
        Event event = create(
                action,
                subject,
                object,
                path);
        eventRepository.save(event);
    }

    public void addEventAccessDenied(String email, String path) {
        Event event = create(
                "ACCESS_DENIED",
                email,
                path,
                path);
        eventRepository.save(event);
    }

    public void addEventLoginFailed(String email, String path) {
        Event event = create(
                "LOGIN_FAILED",
                email,
                path,
                path);
        eventRepository.save(event);
    }

    public void addEventLockUser(String subjectEmail, String objectEmail, String path) {
        Event event = create(
                "LOCK_USER",
                subjectEmail,
                "Lock user " + objectEmail,
                path);
        eventRepository.save(event);
    }

    public void addEventUnlockUser(String subjectEmail, String objectEmail, String path) {
        Event event = create(
                "UNLOCK_USER",
                subjectEmail,
                "Unlock user " + objectEmail,
                path);
        eventRepository.save(event);
    }

    public void addEventBruteForce(String email, String path) {
        Event event = create(
                "BRUTE_FORCE",
                email,
                path,
                path);
        eventRepository.save(event);
    }

    private String getSubject(UserDetails userDetails) {
        if (userDetails == null) {
            return "Anonymous";
        }
        return userDetails.getUsername();
    }

    private Event create(String action, String email, String object, String path) {
        return Event.builder()
                .date(new Date())
                .action(action)
                .email(email)
                .object(object)
                .path(path)
                .build();
    }
}
