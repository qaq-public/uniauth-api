package uniauth.model.dto;

import java.util.List;

public record UpdateProjectMemberDto(List<Integer> roleIds, String remark) {
}

