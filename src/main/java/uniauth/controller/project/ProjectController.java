package uniauth.controller.project;

import com.qaq.base.annotation.CheckPermission;
import com.qaq.base.model.Auth;
import com.qaq.base.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.GameType;
import uniauth.enums.PermissionConstant;
import uniauth.event.AccessProjectEvent;
import uniauth.jpa.entity.AccessProject;
import uniauth.jpa.entity.Project;
import uniauth.jpa.repository.*;
import uniauth.model.vo.UserProjectPermissionVO;
import uniauth.model.dto.CreateProjectDto;
import uniauth.model.dto.UpdateProjectDto;
import uniauth.service.PermissionQueryService;
import uniauth.service.UserRoleService;
import uniauth.jpa.entity.Permission;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/projects")
@RestController
public class ProjectController {

    private final AppRepository appRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PermissionQueryService permissionQueryService;
    private final AccessProjectRepository accessProjectRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<String> appTypes = Arrays.stream(GameType.values()).map(GameType::getValue).toList();
    private final UserRoleService userRoleService;
    private final PermissionRepository permissionRepository;

    /**
     * uniauth-web 鉴权使用
     */
    @GetMapping(value = "/{projectId}/user")
    public ApiResponse<UserProjectPermissionVO> user(@PathVariable String projectId, @RequestAttribute Auth auth) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var app = appRepository.findByCode("uniauth").orElseThrow();
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        if (userRoleService.isPlatformAdmin(user.getId())){
            return new ApiResponse<>(new UserProjectPermissionVO(permissionRepository.findByAppId(app.getId()).stream().map(Permission::getName).collect(Collectors.toSet())));
        }

        var permissionSet = permissionQueryService.getUserAppProjectPermission(app.getId(), project.getId(), user.getId());
        return new ApiResponse<>(new UserProjectPermissionVO(permissionSet));
    }

    @GetMapping(value = "")
    public ApiResponse<List<Project>> list() {
        return new ApiResponse<>(projectRepository.findAll());
    }

    @PostMapping("")
    public ApiResponse<Project> create(@Valid @RequestBody CreateProjectDto params, @RequestAttribute Auth auth) {
        var user = userRepository.findByOpenId(auth.getToken().getOpenid()).orElseThrow();
        if (projectRepository.existsByCode(params.code())) {
            return new ApiResponse<>(-1, null, "该项目已接入,请直接申请加入该项目");
        }
        if (accessProjectRepository.existsByCode(params.code())) {
            return new ApiResponse<>(-1, null, "接入申请已在审核中，请勿重复申请");
        }
        var accessProject = AccessProject.builder()
                .code(params.code())
                .name(params.name())
                .studio(params.studio())
                .stage(params.stage())
                .avatar(params.avatar())
                .description(params.description())
                .gameType(params.game_type())
                .createUser(user)
                .createTime(new Date())
                .status(0)
                .build();
        accessProjectRepository.save(accessProject);
        applicationEventPublisher.publishEvent(new AccessProjectEvent(accessProject.getId()));
        return new ApiResponse<>(null, null);
    }

    @GetMapping(value = "/{projectId}")
    public ApiResponse<Project> retrieve(@PathVariable String projectId) {
        var project = projectRepository.findByCode(projectId).orElseThrow();
        return new ApiResponse<>(project);
    }

    @CheckPermission(PermissionConstant.PROJECT_MODIFY)
    @PutMapping(value = "/{projectId}")
    public ApiResponse<Project> update(@PathVariable String projectId, @Valid @RequestBody UpdateProjectDto params) {
        var oldProject = projectRepository.findByCode(projectId).orElseThrow();
        oldProject.setName(params.name());
        oldProject.setGameType(params.game_type());
        oldProject.setStudio(params.studio());
        oldProject.setStage(params.stage());
        oldProject.setDescription(params.description());
        oldProject.setAvatar(params.avatar());
        projectRepository.save(oldProject);
        return new ApiResponse<>(oldProject);
    }

    /**
     * 项目类型列表
     */
    @GetMapping(value = "/types")
    public ApiResponse<List<String>> getAppTypes() {
        return new ApiResponse<>(appTypes);
    }

    /**
     * 以gameTYpe分类 查询项目以游戏类型分组
     */
    @GetMapping(value = "/projectsMap")
    public ApiResponse<Map<String, List<Project>>> getProjectGroupByMap() {
        LinkedHashMap<String, List<Project>> projectMap = new LinkedHashMap<>();
        List<Project> noTypes = new ArrayList<>();
        var projects = projectRepository.findAll();
        projectMap.put("全部", projects);
        for (String s : appTypes) {
            projectMap.put(s, new ArrayList<>());
        }
        for (var project : projects) {
            var types = project.getGameType();
            if (types == null || types.isEmpty()) {
                noTypes.add(project);
            } else {
                for (var gameType : types) {
                    projectMap.computeIfAbsent(gameType, k -> new ArrayList<>()).add(project);
                }
            }
        }
        projectMap.put("无类型", noTypes);
        return new ApiResponse<>(projectMap);
    }
}
