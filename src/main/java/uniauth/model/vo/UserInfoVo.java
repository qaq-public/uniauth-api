package uniauth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uniauth.jpa.entity.Project;
import uniauth.jpa.entity.User;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoVo {
    private User user;
    private List<Project> projects;
    private List<String> permissions;
}