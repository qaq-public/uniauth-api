package uniauth.controller.app;

import com.qaq.base.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.entity.AppMember;
import uniauth.jpa.repository.AppMemberRepository;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.AppRoleRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.dto.CreateAppMemberDto;
import uniauth.model.dto.UpdateAppMemberDto;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class AppMemberController {

    private final AppRepository appRepository;
    private final UserRepository userRepository;
    private final AppMemberRepository appMemberRepository;
    private final AppRoleRepository appRoleRepository;

    @GetMapping(value = "/{appId}/members")
    public ApiResponse<List<AppMember>> list(@PathVariable String appId) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var members = appMemberRepository.findByAppId(app.getId());
        return new ApiResponse<>(members);
    }

    @PostMapping(value = "/{appId}/members")
    public ApiResponse<Object> create(@PathVariable String appId, @RequestBody CreateAppMemberDto params) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var users = userRepository.findByOpenIdIn(params.userIds());
        users.forEach(user -> {
            var appMember = appMemberRepository.findByUserIdAndAppId(user.getId(), app.getId()).orElse(AppMember.builder().app(app).user(user).build());
            appMember.setRoles(new HashSet<>(appRoleRepository.findByIdIn(params.roleIds())));
            appMemberRepository.save(appMember);
        });
        return new ApiResponse<>(null);
    }

    @PutMapping(value = "/members/{id}")
    public ApiResponse<?> update(@PathVariable Integer id,
                                 @RequestBody UpdateAppMemberDto params) {
        var appMember = appMemberRepository.findById(id).orElseThrow();
        appMember.setRoles(new HashSet<>(appRoleRepository.findByIdIn(params.roleIds())));
        appMemberRepository.save(appMember);
        return new ApiResponse<>();
    }

    @DeleteMapping(value = "/members/{id}")
    public ApiResponse<Object> destroy(@PathVariable Integer id) {
        appMemberRepository.deleteById(id);
        return new ApiResponse<>(null);
    }
}
