package uniauth.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import uniauth.jpa.entity.AccessProject;
import uniauth.jpa.entity.App;

@Setter
@Getter
public class AccessAppEvent extends ApplicationEvent {
    private Integer id;

    public AccessAppEvent(Integer id) {
        super(new Object());
        this.id = id;
    }
}
