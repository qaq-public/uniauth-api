package uniauth.controller.app;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qaq.base.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uniauth.jpa.entity.ProjectRole;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.PermissionRepository;
import uniauth.jpa.repository.ProjectRoleRepository;
import uniauth.model.dto.UpdateProjectRolePermissionDto;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class ProjectRolePermissionController {

    private final AppRepository appRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * App项目角色权限
     *
     * @param appCode
     * @return
     */
    @GetMapping(value = "/{appCode}/project_role_permission")
    public ApiResponse<List<ProjectRole>> list(@PathVariable String appCode) {
        var app = appRepository.findByCode(appCode).orElseThrow();
        var projectRoles = projectRoleRepository.findAll().stream()
                .map(role -> {
                    var filteredPermissions = role.getPermissions().stream()
                            .filter(permission -> permission.getApp().getId().equals(app.getId()))
                            .collect(Collectors.toSet());
                    role.setPermissions(filteredPermissions);
                    return role;
                })
                .toList();
        return new ApiResponse<>(projectRoles);
    }

    @PutMapping(value = "/project_role_permission/{roleId}")
    public ApiResponse<?> update(
            @PathVariable Integer roleId,
            @RequestBody UpdateProjectRolePermissionDto roleParams) {
        var projectRole = projectRoleRepository.findById(roleId).orElseThrow();
        projectRole.setPermissions(new HashSet<>(permissionRepository.findAllById(roleParams.permissionIdList())));
        projectRoleRepository.save(projectRole);
        return new ApiResponse<>();
    }

}
