package uniauth.model.dto;

import java.util.List;

public record CreateProjectMemberDto(
        String userIdType,
        List<String> userIds,
        List<Integer> roleIds,
        String remark
) {}
