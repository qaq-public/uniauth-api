package uniauth.model.dto;

import java.util.List;

public record CreateAppMemberDto(List<String> userIds, List<Integer> roleIds) {
}
