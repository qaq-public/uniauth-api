package uniauth.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserVo {
    private Integer id;
    private String name;
    @JsonProperty("userid")
    private String userId;
    @JsonProperty("openid")
    private String openId;
    private String nickname;
    private String email;
    private String avatar;
    private Boolean leaver;
    @JsonProperty("git_email")
    private String gitEmail;
}
