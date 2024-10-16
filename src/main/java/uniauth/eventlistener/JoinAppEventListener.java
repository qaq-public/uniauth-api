package uniauth.eventlistener;

import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uniauth.event.ApproveJoinAppEvent;
import uniauth.event.JoinAppEvent;
import uniauth.event.RejectJoinAppEvent;
import uniauth.jpa.entity.AppRole;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.JoinAppRepository;
import uniauth.service.NotifyService;
import uniauth.service.UserService;

import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JoinAppEventListener {

    final private NotifyService notifyService;
    final private JoinAppRepository joinAppRepository;
    final private UserService userService;

    @Value("${qaq.host}")
    private String qaqHost;

    @Value("${feishu.card.join_app}")
    private String joinAppCardId;

    @Value("${feishu.card.join_app_success}")
    private String joinAppSuccessCardId;

    @Value("${feishu.card.join_app_fail}")
    private String joinAppFailCardId;

    @Async
    @EventListener({JoinAppEvent.class})
    public void join(JoinAppEvent event) {
        var join = joinAppRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser(). getOpenId());
        variable.put("appName", join.getApp().getName());
        variable.put("link","https://" + qaqHost + "/uniauth/apps/" + join.getApp().getName());
        var projectAdmins = userService.listAppAdmin(join.getApp());
        if (!projectAdmins.isEmpty()) {
            notifyService.sendCard(joinAppCardId, variable, projectAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
    }

    @Async
    @EventListener({ApproveJoinAppEvent.class})
    public void approve(ApproveJoinAppEvent event) {
        var join = joinAppRepository.findById(event.getId()).orElseThrow();
        log.info("ApproveJoinProjectEvent: {} {}", event.getId(), join);
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser().getOpenId());
        variable.put("appName", join.getApp().getName());
        var roles = join.getAppRoles().stream().map(AppRole::getName).collect(Collectors.joining(","));
        variable.put("roles", roles);
        variable.put("approvalUser", join.getApproveUser().getOpenId());
        var appAdmins = userService.listAppAdmin(join.getApp());
        if (!appAdmins.isEmpty()) {
            notifyService.sendCard(joinAppSuccessCardId, variable, appAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
        notifyService.sendCard(joinAppSuccessCardId, variable, join.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }

    @Async
    @EventListener({RejectJoinAppEvent.class})
    public void reject(RejectJoinAppEvent event) {
        var join = joinAppRepository.findById(event.getId()).orElseThrow();
        var variable = new HashMap<String, Object>();
        variable.put("open_id", join.getCreateUser().getOpenId());
        variable.put("appName", join.getApp().getName());
        variable.put("approvalUser", join.getApproveUser().getOpenId());
        var appAdmins = userService.listAppAdmin(join.getApp());
        if (!appAdmins.isEmpty()) {
            notifyService.sendCard(joinAppFailCardId, variable, appAdmins.stream().map(User::getOpenId).toList(), ReceiveIdTypeEnum.OPEN_ID.getValue());
        }
        notifyService.sendCard(joinAppFailCardId, variable, join.getCreateUser().getOpenId(), ReceiveIdTypeEnum.OPEN_ID.getValue());
    }
}

