package uniauth.eventlistener;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uniauth.event.AccessProjectEvent;
import uniauth.event.ApproveAccessProjectEvent;
import uniauth.jpa.repository.AccessProjectRepository;
import uniauth.service.NotifyService;

import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class AccessProjectEventListener {

    private final NotifyService notifyService;
    private final AccessProjectRepository accessProjectRepository;

    @Value("${qaq.host}")
    private String qaqHost;

    @Value("${feishu.card.create_project}")
    private String createProjectCardId;

    @Value("${feishu.card.create_project_success}")
    private String createProjectSuccessCardId;

    @Async
    @EventListener({AccessProjectEvent.class})
    public void accessProject(AccessProjectEvent event) {
        var accessProject = accessProjectRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("email", accessProject.getCreateUser().getEmail());
        variable.put("projectId",accessProject.getCode());
        variable.put("projectName", accessProject.getName());
        variable.put("studio", accessProject.getStudio());
        variable.put("stage", accessProject.getStage());
        variable.put("description", accessProject.getDescription());
        variable.put("link", "https://" + qaqHost + "/uniauth/admin/audits");
        notifyService.sendCardAdmin(createProjectCardId, variable);
    }

    @Async
    @EventListener({ApproveAccessProjectEvent.class})
    public void approveProject(ApproveAccessProjectEvent event) {
        var accessProject = accessProjectRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("email", accessProject.getCreateUser());
        variable.put("projectId", accessProject.getCode());
        variable.put("projectName", accessProject.getName());
        variable.put("studio", accessProject.getStudio());
        variable.put("stage", accessProject.getStage());
        variable.put("description", accessProject.getDescription());
        variable.put("link", "https://" + qaqHost + "/uniauth/projects/" + accessProject.getCode());
        notifyService.sendCard(createProjectSuccessCardId, variable, accessProject.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        notifyService.sendCard(createProjectSuccessCardId, variable, accessProject.getApproveUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }
}
