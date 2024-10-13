package uniauth.model.dto;

import java.util.Set;

public record CreateProjectDto(
        String code,
        String name,
        String studio,
        String stage,
        String avatar,
        String description,
        Set<String> game_type
) {}
