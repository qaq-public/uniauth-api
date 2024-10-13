package uniauth.controller.app;

import com.qaq.base.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.entity.Permission;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.PermissionRepository;
import uniauth.model.dto.CreateAppPermissionDto;
import uniauth.model.dto.UpdateAppPermissionDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping(value = "/apps")
@RestController
public class AppPermissionController {

    private final PermissionRepository permissionRepository;
    private final AppRepository appRepository;

    @GetMapping(value = "/{appName}/permissions")
    public ApiResponse<List<Permission>> list(@PathVariable String appName) {
        var app = appRepository.findByCode(appName).orElseThrow();
        return new ApiResponse<>(permissionRepository.findByAppId(app.getId()));
    }

    @PostMapping(value = "/{appName}/permissions")
    public ApiResponse<Permission> create(@PathVariable String appName, @Valid @RequestBody CreateAppPermissionDto permissionInfo) {
        var app = appRepository.findByCode(appName).orElseThrow();
        var permission = Permission.builder()
                .app(app)
                .name(permissionInfo.permissionName())
                .description(permissionInfo.description())
                .preDefined(false)
                .build();
        try {
            permissionRepository.save(permission);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(-1, null, "权限名称已存在");
        }
        return new ApiResponse<>(permission);
    }

    @PatchMapping(value = "/permissions/{permissionId}")
    public ApiResponse<Permission> partialUpdate(@RequestBody UpdateAppPermissionDto params,
                                                 @PathVariable Integer permissionId) {
        var permission = permissionRepository.findById(permissionId).orElseThrow();
        permission.setDescription(params.description());
        permissionRepository.save(permission);
        return new ApiResponse<>(permission);
    }

    @DeleteMapping("/permissions/{permissionId}")
    public ApiResponse<Integer> destroy(@PathVariable Integer permissionId) {
        int count = 0;
        var permission = permissionRepository.findById(permissionId).orElseThrow();
        if (Boolean.TRUE.equals(permission.getPreDefined())) {
            return new ApiResponse<>(-1, null, "preDefined permission can't delete");
        }
        try {
            permissionRepository.deleteById(permissionId);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(-1, null, "该权限被角色引用");
        }
        return new ApiResponse<>(count);
    }
}
