package uniauth.eventlistener;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uniauth.event.*;
import uniauth.jpa.entity.AppRole;
import uniauth.jpa.entity.ProjectRole;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.JoinProjectRepository;
import uniauth.service.NotifyService;
import uniauth.service.UserService;

import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JoinProjectEventListener {

    final private NotifyService notifyService;
    final private JoinProjectRepository joinProjectRepository;
    final private UserService userService;

    @Value("${qaq.host}")
    private String qaqHost;

    @Value("${feishu.card.join_project}")
    private String joinProjectCardId;

    @Value("${feishu.card.join_project_success}")
    private String joinProjectSuccessCardId;

    @Value("${feishu.card.join_project_fail}")
    private String joinProjectFailCardId;

    @Async
    @EventListener({JoinProjectEvent.class})
    public void join(JoinProjectEvent event) {
        var join = joinProjectRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser().getOpenId());
        variable.put("projectId", join.getProject().getCode());
        variable.put("link", "https://" + qaqHost + "/uniauth/projects/" + join.getProject().getCode());
        var projectAdmins = userService.listProjectAdmin(join.getProject());
        if(!projectAdmins.isEmpty()) {
            notifyService.sendCard(joinProjectCardId, variable, projectAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
    }

    @Async
    @EventListener({ApproveJoinProjectEvent.class})
    public void approve(ApproveJoinProjectEvent event) {
        var join = joinProjectRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser().getOpenId());
        variable.put("projectId", join.getProject().getCode());
        var roles = join.getRoles().stream().map(ProjectRole::getName).collect(Collectors.joining(","));
        variable.put("roles", roles);
        variable.put("approvalUser", join.getApproveUser().getOpenId());
        var projectAdmins = userService.listProjectAdmin(join.getProject());
        if(!projectAdmins.isEmpty()) {
            notifyService.sendCard(joinProjectSuccessCardId, variable, projectAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
        notifyService.sendCard(joinProjectSuccessCardId, variable, join.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }

    @Async
    @EventListener({RejectJoinProjectEvent.class})
    public void reject(RejectJoinProjectEvent event) {
        var join = joinProjectRepository.findById(event.getId()).orElseThrow();
        log.info("ApproveJoinProjectEvent: {} {}", event.getId(), join);
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser().getOpenId());
        variable.put("projectId", join.getProject().getCode());
        variable.put("approvalUser", join.getApproveUser().getOpenId());
        var projectAdmins = userService.listProjectAdmin(join.getProject());
        if(!projectAdmins.isEmpty()) {
            notifyService.sendCard(joinProjectFailCardId, variable, projectAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
        notifyService.sendCard(joinProjectFailCardId, variable, join.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }
}

