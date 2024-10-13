package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class RejectJoinProjectEvent extends ApplicationEvent {
    private Integer id;

    public RejectJoinProjectEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
