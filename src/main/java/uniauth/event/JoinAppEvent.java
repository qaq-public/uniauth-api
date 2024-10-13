package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class JoinAppEvent extends ApplicationEvent {
    private Integer id;

    public JoinAppEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
