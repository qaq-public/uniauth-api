package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class AccessProjectEvent extends ApplicationEvent {
    private Integer id;

    public AccessProjectEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
