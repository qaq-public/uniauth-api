package uniauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uniauth.jpa.entity.Permission;
import uniauth.jpa.repository.PermissionRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PermissionQueryService {
    private final PermissionRepository permissionRepository;
    private final UserRoleService userRoleService;

    public Set<String> getUserAppPermission(Integer appId, Integer userId) {
        if (userRoleService.isAppAdmin(userId, appId)) {
            return permissionRepository.findByAppId(appId).stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
        }
        // 查询在App层级拥有的权限
        var userAppPermission = permissionRepository.findPermissionNamesByUserAndApp(userId, appId);
        var userAppExtraPermission = permissionRepository.findExtraPermissionNamesByUserAndApp(userId, appId);
        Set<String> set = new HashSet<>();
        set.addAll(userAppPermission);
        set.addAll(userAppExtraPermission);
        return set;
    }

    // 返回这个用户在这个项目的项目层和应用层的权限
    public Set<String> getUserAppProjectPermission(Integer appId, Integer projectId, Integer userId) {
        var appPermissions = getUserAppPermission(appId, userId);

        var appProjectPermissions= permissionRepository.findPermissionNamesByUserAndProjectAndApp(userId, projectId, appId);

        appPermissions.addAll(appProjectPermissions);
        return appPermissions;
    }
}
