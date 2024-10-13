package uniauth.controller.app;

import com.qaq.base.annotation.CheckPermission;
import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.DbConstant;
import uniauth.enums.PermissionConstant;
import uniauth.event.AccessAppEvent;
import uniauth.jpa.entity.App;
import uniauth.jpa.entity.AppMember;
import uniauth.jpa.entity.AppRole;
import uniauth.jpa.repository.AppMemberRepository;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.AppRoleRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.dto.UpdateAppDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class AppController {

    private final AppRepository appRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppRoleRepository appRoleRepository;
    private final UserRepository userRepository;
    private final AppMemberRepository appMemberRepository;

    /**
     * uniauth-web 鉴权使用
     */
    @GetMapping("/{appName}/user")
    public ApiResponse<List<AppRole>> user(@PathVariable String appName, @RequestAttribute Auth auth) {
        var app = appRepository.findByCode(appName).orElseThrow();
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        var appMemberOptional = appMemberRepository.findByUserIdAndAppId(user.getId(), app.getId());
        if (appMemberOptional.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        }
        var appMember = appMemberOptional.get();
        var appAdmin = appRoleRepository.findById(DbConstant.ADMIN).orElseThrow();
        if ( appMember.getRoles().contains(appAdmin)) {
            var roles = appRoleRepository.findByAppId(app.getId());
            roles.add(appAdmin);
            return new ApiResponse<>(roles);
        } else {
            return new ApiResponse<>(appMember.getRoles().stream().toList());
        }
    }

    @GetMapping("")
    public ApiResponse<List<App>> list() {
        return new ApiResponse<>(appRepository.findAll());
    }

    @CheckPermission(PermissionConstant.APP_CREATE)
    @PostMapping(value = "")
    public ApiResponse<App> create(@RequestBody App app, @RequestAttribute Auth auth) {
        var oldProjectOptional = appRepository.findByCode(app.getName());
        if (oldProjectOptional.isPresent()) {
            return new ApiResponse<>(-1, null, "该应用已接入,请勿重新申请");
        }
        app.setId(null);
        app.setCreateUser(userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow());
        app.setCreateTime(new Date());
        appRepository.save(app);
        var appMember = AppMember.builder()
                .user(userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow())
                .app(app)
                .roles(new HashSet<>(List.of(appRoleRepository.findById(DbConstant.ADMIN).orElseThrow())))
                .build();
        appMemberRepository.save(appMember);
        applicationEventPublisher.publishEvent(new AccessAppEvent(app.getId()));
        return new ApiResponse<>(app);
    }

    @GetMapping(value = "/{appName}")
    public ApiResponse<App> retrieve(@PathVariable String appName) {
        var app = appRepository.findByCode(appName).orElseThrow();
        return new ApiResponse<>(app);
    }

    @PutMapping(value = "/{appCode}")
    public ApiResponse<App> update(@RequestBody UpdateAppDto params,
                                   @PathVariable String appCode) {
        var app = appRepository.findByCode(appCode).orElseThrow();
        app.setName(params.appNickname());
        app.setDescription(params.description());
        app.setAvatar(params.iconUrl());
        appRepository.save(app);
        return new ApiResponse<>(app);
    }

}
