package account.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "action", "subject", "object", "path" })
public class EventDTO {
    @JsonIgnore
    private int id;
    @JsonIgnore
    private Date date;
    private String action;
    @JsonProperty(value = "subject")
    private String email;
    private String object;
    private String path;
}
