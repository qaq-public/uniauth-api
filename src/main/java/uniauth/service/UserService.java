package uniauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uniauth.jpa.entity.App;
import uniauth.jpa.entity.Project;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.vo.UserVo;
import uniauth.util.UserConverter;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public List<UserVo> list() {
        return userRepository.findAll().stream().map(UserConverter::convertToAdminUserVo).toList();
    }

    public List<User> listProjectAdmin(Project project) {
        return userRepository.findProjectAdmins(project.getId());
    }

    public List<User> listAppAdmin(App app) {
        return userRepository.findAppAdmins(app.getId());
    }

    public List<User> list(List<String> userIds, String userIdType) {
        return userRepository.findByOpenIdIn(userIds);
    }

    public User createOrUpdateUser(User user) {
        User oldUser = userRepository.findByOpenId(user.getOpenId()).orElse(null);
        var timeMillis = System.currentTimeMillis();
        if (oldUser != null) {
            oldUser.setName(user.getName());
            oldUser.setUserId(user.getUserId());
            oldUser.setOpenId(user.getOpenId());
            oldUser.setNickname(user.getNickname());
            oldUser.setAvatar(user.getAvatar());

            oldUser.setAccessToken(user.getAccessToken());
            oldUser.setTokenType(user.getTokenType());
            oldUser.setExpiresIn(timeMillis + user.getExpiresIn() * 1000);
            oldUser.setRefreshToken(user.getRefreshToken());
            oldUser.setRefreshExpiresIn(timeMillis + user.getRefreshExpiresIn() * 1000);

            userRepository.save(oldUser);
            return oldUser;
        } else {
            user.setExpiresIn(timeMillis + user.getExpiresIn() * 1000L);
            user.setRefreshExpiresIn(timeMillis + user.getRefreshExpiresIn() * 1000L);
            userRepository.save(user);
            return user;
        }
    }

}
