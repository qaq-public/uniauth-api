package uniauth.eventlistener.login;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import uniauth.event.LoginEvent;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class LoginEventListener {

    private final UserRepository userRepository;

    @Async
    @EventListener({LoginEvent.class})
    public void handle(LoginEvent event) {
        var oldUserOptional = userRepository.findByOpenId(event.getUserInfo().getOpenId());
        var timeMillis = System.currentTimeMillis();
        if (oldUserOptional.isPresent()) {
            var oldUser = oldUserOptional.get();
            oldUser.setName(event.getUserInfo().getName());
            oldUser.setUserId(event.getUserInfo().getUserId());
            oldUser.setOpenId(event.getUserInfo().getOpenId());
            oldUser.setNickname(event.getUserInfo().getName());
            oldUser.setAvatar(event.getUserInfo().getAvatarUrl());

            oldUser.setAccessToken(event.getUserAccessToken().getAccessToken());
            oldUser.setTokenType(event.getUserAccessToken().getTokenType());
            oldUser.setExpiresIn(timeMillis + event.getUserAccessToken().getExpiresIn() * 1000);
            oldUser.setRefreshToken(event.getUserAccessToken().getRefreshToken());
            oldUser.setRefreshExpiresIn(timeMillis + event.getUserAccessToken().getRefreshExpiresIn() * 1000);
            userRepository.save(oldUser);
        } else {
            var user = User.builder()
                    .name(event.getUserInfo().getName())
                    .userId(event.getUserInfo().getUserId())
                    .openId(event.getUserInfo().getOpenId())
                    .nickname(event.getUserInfo().getName())
                    .email(event.getUserInfo().getEmail())
                    .avatar(event.getUserInfo().getAvatarUrl())
                    .accessToken(event.getUserAccessToken().getAccessToken())
                    .tokenType(event.getUserAccessToken().getTokenType())
                    .expiresIn(timeMillis + event.getUserAccessToken().getExpiresIn() * 1000)
                    .refreshToken(event.getUserAccessToken().getRefreshToken())
                    .refreshExpiresIn(timeMillis + event.getUserAccessToken().getRefreshExpiresIn() * 1000)
                    .leaver(false)
                    .build();
            userRepository.save(user);
        }
    }
}
