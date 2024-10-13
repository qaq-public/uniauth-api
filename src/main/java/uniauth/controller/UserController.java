package uniauth.controller;

import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.entity.User;
import uniauth.jpa.repository.UserRepository;
import uniauth.model.dto.PartialUpdateUserDto;
import uniauth.model.vo.UserVo;
import uniauth.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping(value = "/users")
    public ApiResponse<List<UserVo>> list() {
        return new ApiResponse<>(userService.list());
    }

    /**
     * 添加一个用户到平台
     */
    @PostMapping("/users")
    public ApiResponse<User> create(@RequestBody User user) {
        return new ApiResponse<>(userService.createOrUpdateUser(user));
    }

    /**
     * 修改用户gitEmail
     */
    @PatchMapping("/users/{email:.+}")
    public ApiResponse<User> partialUpdate(@PathVariable String email, @RequestBody PartialUpdateUserDto patchUser) {
        var user = userRepository.findByEmail(email).orElseThrow();
        user.setGitEmail(patchUser.gitEmail());
        userRepository.save(user);
        return new ApiResponse<>(user);
    }

    /**
     * 离职
     */
    @DeleteMapping("/users/{email:.+}")
    public ApiResponse<User> destroyUser(@PathVariable String email) {
        var user = userRepository.findByEmail(email).orElseThrow();
        user.setLeaver(true);
        userRepository.save(user);
        return new ApiResponse<>(user);
    }
}
