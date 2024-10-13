package uniauth.eventlistener;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uniauth.event.AccessAppEvent;
import uniauth.jpa.repository.AppRepository;
import uniauth.service.NotifyService;

import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class AppEventListener {

    final private NotifyService notifyService;
    final private AppRepository appRepository;

    @Value("${qaq.host}")
    private String qaqHost;

    @Value("${feishu.card.create_app_success}")
    private String createAppSuccessCardId;

    @Async
    @EventListener({AccessAppEvent.class})
    public void accessApp(AccessAppEvent event) {
        var app = appRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("appName", app.getCode());
        variable.put("appNickname", app.getName());
        variable.put("description", app.getDescription());
        variable.put("email", app.getCreateUser().getEmail());
        variable.put("link", "https://" + qaqHost + "/uniauth/apps/" + app.getCode());
        notifyService.sendCardAdmin(createAppSuccessCardId, variable);
        notifyService.sendCard(createAppSuccessCardId, variable, app.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }
}
