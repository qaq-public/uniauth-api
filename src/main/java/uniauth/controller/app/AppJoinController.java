package uniauth.controller.app;

import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import uniauth.event.ApproveJoinAppEvent;
import uniauth.event.JoinAppEvent;
import uniauth.event.RejectJoinAppEvent;
import uniauth.jpa.entity.AppMember;
import uniauth.jpa.entity.JoinApp;
import uniauth.jpa.repository.*;
import uniauth.model.dto.ApproveAppJoinDto;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class AppJoinController {

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final JoinAppRepository joinAppRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppRoleRepository appRoleRepository;
    private final AppMemberRepository appMemberRepository;

    @GetMapping("/{appId}/joins")
    public ApiResponse<List<JoinApp>> list(@PathVariable String appId) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var appJoinApplies = joinAppRepository.findByAppIdOrderByCreateTimeDesc(app.getId());
        return new ApiResponse<>(appJoinApplies);
    }

    @PostMapping("/{appId}/joins")
    public ApiResponse<JoinApp> create(@PathVariable String appId, @RequestAttribute Auth auth) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var user = userRepository.findByEmail(auth.getToken().getEmail()).orElseThrow();
        if (joinAppRepository.existsByAppIdAndCreateUserAndApproveTime(app.getId(), user, null)) {
            return new ApiResponse<>(-1, null, "你已申请加入该应用, 请勿重复申请");
        }
        var joinApp = JoinApp
                .builder()
                .app(app)
                .createUser(user)
                .createTime(new Date())
                .status(0)
                .build();
        joinAppRepository.save(joinApp);
        applicationEventPublisher.publishEvent(new JoinAppEvent(joinApp.getId()));
        return new ApiResponse<>(joinApp);
    }

    @PatchMapping("/joins/{id}/approve")
    public ApiResponse<Object> update(@PathVariable Integer id,
                                      @RequestBody ApproveAppJoinDto params,
                                      @RequestAttribute Auth auth) {
        var joinApp = joinAppRepository.findById(id).orElseThrow();
        var approveUser = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        var appMemberOptional = appMemberRepository.findByUserIdAndAppId(joinApp.getCreateUser().getId(), joinApp.getApp().getId());
        if (appMemberOptional.isPresent()) {
            return new ApiResponse<>(-1, null, "用户已是应用成员");
        }
        var roles = appRoleRepository.findByIdIn(params.roleIds());

        var appMember = AppMember.builder()
                .app(joinApp.getApp())
                .user(joinApp.getCreateUser())
                .roles(new HashSet<>(roles))
                .build();
        appMemberRepository.save(appMember);

        joinApp.setApproveUser(approveUser);
        joinApp.setApproveTime(new Date());
        joinApp.setAppRoles(roles);
        joinApp.setStatus(1);
        joinAppRepository.save(joinApp);
        applicationEventPublisher.publishEvent(new ApproveJoinAppEvent(joinApp.getId()));
        return new ApiResponse<>(null);
    }

    @PatchMapping("/joins/{id}/reject")
    public ApiResponse<Object> reject(@PathVariable Integer id,
                                      @RequestAttribute Auth auth) {
        var joinApp = joinAppRepository.findById(id).orElseThrow();
        var approveUser = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        joinApp.setApproveUser(approveUser);
        joinApp.setApproveTime(new Date());
        joinApp.setStatus(2);
        joinAppRepository.save(joinApp);
        applicationEventPublisher.publishEvent(new RejectJoinAppEvent(joinApp.getId()));
        return new ApiResponse<>(null);
    }
}
