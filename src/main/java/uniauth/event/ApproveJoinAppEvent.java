package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class ApproveJoinAppEvent extends ApplicationEvent {
    private Integer id;

    public ApproveJoinAppEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
