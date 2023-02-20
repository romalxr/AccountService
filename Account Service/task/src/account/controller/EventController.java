package account.controller;

import account.dto.EventDTO;
import account.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    EventService eventService;

    @GetMapping("/security/events")
    public List<EventDTO> getEventList() {
        return eventService.findAll();
    }

}
