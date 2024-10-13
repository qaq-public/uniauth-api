package uniauth.model.dto;

import java.util.List;

public record CreateAppRoleDto(String roleName, String description, List<Integer> permissionIdList) {
}
