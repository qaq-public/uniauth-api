package uniauth.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uniauth.jpa.entity.User;
import uniauth.model.vo.UserVo;

public class UserConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static UserVo convertToAdminUserVo(User user) {
        if (user == null) {
            return null;
        }

        // 使用ObjectMapper将User对象转换为AdminUserVo对象
        var adminSUserVo =  objectMapper.convertValue(user, UserVo.class);
        adminSUserVo.setGitEmail(user.getGitEmail());
        adminSUserVo.setLeaver(user.getLeaver());
        return adminSUserVo;
    }
}
