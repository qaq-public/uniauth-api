package uniauth.controller.app;

import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uniauth.enums.DbConstant;
import uniauth.jpa.entity.AppRole;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.AppRoleRepository;
import uniauth.jpa.repository.PermissionRepository;
import uniauth.model.dto.CreateAppRoleDto;
import uniauth.model.dto.UpdateAppRoleDto;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class AppRoleController {

    private final AppRepository appRepository;
    private final AppRoleRepository appRoleRepository;
    private final PermissionRepository permissionRepository;

    @GetMapping(value = "/{appId}/roles")
    public ApiResponse<List<AppRole>> list(@PathVariable String appId ) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var roles = appRoleRepository.findByAppId(app.getId());
        // 增加一些必须的角色
        var appAdmin = appRoleRepository.findById(DbConstant.ADMIN).orElseThrow();
        appAdmin.setPermissions(new HashSet<>(permissionRepository.findByAppId(app.getId())));
        roles.add(appAdmin);
        return new ApiResponse<>(roles);
    }

    @PostMapping(value = "/{appId}/roles")
    public ApiResponse<AppRole> create(@PathVariable String appId, @RequestBody CreateAppRoleDto roleParams) {
        var app = appRepository.findByCode(appId).orElseThrow();
        var roleOptional = appRoleRepository.findByNameAndAppId(roleParams.roleName(), app.getId());
        if (roleOptional.isPresent()) {
            return new ApiResponse<>(-1, null, "roleName already exist");
        } else {
            var role = AppRole.builder()
                    .name(roleParams.roleName())
                    .description(roleParams.description())
                    .app(app)
                    .preDefined(false)
                    .permissions(new HashSet<>(permissionRepository.findAllById(roleParams.permissionIdList())))
                    .build();
            appRoleRepository.save(role);
            return new ApiResponse<>(role);
        }
    }

    @PutMapping(value = "/roles/{roleId}")
    public ApiResponse<AppRole> update(@PathVariable Integer roleId,
                                            @RequestBody UpdateAppRoleDto roleParams) {
        var role = appRoleRepository.findById(roleId).orElseThrow();
        role.setName(roleParams.roleName());
        role.setDescription(roleParams.description());
        role.setPermissions(new HashSet<>(permissionRepository.findAllById(roleParams.permissionIdList())));
        appRoleRepository.save(role);
        return new ApiResponse<>(null, "");
    }

    @DeleteMapping(value = "/roles/{roleId}")
    public ApiResponse<AppRole> destroy(@PathVariable Integer roleId) {
        appRoleRepository.deleteById(roleId);
        return new ApiResponse<>(null, "");
    }
}
