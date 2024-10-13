package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import uniauth.jpa.entity.AccessProject;

@Setter
@Getter
public class ApproveAccessProjectEvent extends ApplicationEvent {
    private Integer id;

    public ApproveAccessProjectEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
