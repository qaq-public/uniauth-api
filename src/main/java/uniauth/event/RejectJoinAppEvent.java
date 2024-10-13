package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class RejectJoinAppEvent extends ApplicationEvent {
    private Integer id;

    public RejectJoinAppEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
