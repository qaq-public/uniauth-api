package uniauth.model.dto;

import java.util.List;

public record UpdateAppRoleDto(String roleName, String description, List<Integer> permissionIdList) {
}
