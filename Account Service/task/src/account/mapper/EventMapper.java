package account.mapper;

import account.dto.EventDTO;
import account.entity.Event;

public class EventMapper {

    public static EventDTO toDTO(Event event){
        return EventDTO.builder()
                .id(event.getId())
                .date(event.getDate())
                .action(event.getAction())
                .email(event.getEmail())
                .object(event.getObject())
                .path(event.getPath())
                .build();
    }
}
