package uniauth.controller;

import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.entity.App;
import uniauth.jpa.entity.Project;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.ProjectRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.vo.UserInfoVo;
import uniauth.model.dto.UpdateDefaultProjectDto;
import uniauth.service.PermissionQueryService;
import uniauth.service.UserRoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class UserAuthorityController {

    private final UserRepository userRepository;
    private final AppRepository appRepository;
    private final PermissionQueryService permissionQueryService;
    private final ProjectRepository projectRepository;
    private final UserRoleService userRoleService;

    /**
     * 网页初始化必调这个接口！！！
     */
    @GetMapping(value = "/user")
    public ApiResponse<UserInfoVo> user(@RequestParam String appName, @RequestAttribute Auth auth) {
        var app = appRepository.findByCode(appName).orElseThrow();
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        var actualProjects = user.getJoinedProjects();

        if (user.getDefaultProject() == null && !actualProjects.isEmpty() ) {
            user.setDefaultProject(actualProjects.getFirst());
            userRepository.save(user);
        }
        List<Project> appProjects = actualProjects;

        if (userRoleService.isAppAdmin(user.getId(), app.getId())) {
            appProjects = projectRepository.findAll();
        }


        Set<String> permissions;
        if (user.getDefaultProject() != null) {
            permissions = permissionQueryService.getUserAppProjectPermission(app.getId(), user.getDefaultProject().getId(), user.getId());

        } else {
            permissions = new HashSet<>();
        }
        var userInfoVo = UserInfoVo.builder()
                .user(user)
                .projects(appProjects)
                .permissions(permissions.stream().toList())
                .build();
        return new ApiResponse<>(userInfoVo);
    }

    @PatchMapping(value = "/user")
    public ApiResponse<Boolean> setDefaultProjectV2(@RequestBody @Valid UpdateDefaultProjectDto params, @RequestAttribute Auth auth) {
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        var project = projectRepository.findByCode(params.project_code()).orElseThrow();
        user.setDefaultProject(project);
        userRepository.save(user);
        return new ApiResponse<>(true, "");
    }

    /**
     * 个人中心-我的应用
     */
    @GetMapping(value = "/user/apps")
    public ApiResponse<List<App>> listApp(@RequestAttribute Auth auth) {
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        return new ApiResponse<>(user.getJoinedApps());
    }

    /**
     * 个人中心-我的项目
     */
    @GetMapping(value = "/user/projects")
    public ApiResponse<List<Project>> listProject(@RequestAttribute Auth auth) {
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        return new ApiResponse<>(user.getJoinedProjects());
    }
}
