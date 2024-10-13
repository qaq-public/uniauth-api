package uniauth.controller;

import com.qaq.base.component.HttpUtil;
import com.qaq.base.model.Auth;
import com.qaq.base.model.uniauth.AuthResult;
import com.qaq.base.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import uniauth.jpa.repository.AppRepository;
import uniauth.jpa.repository.ProjectRepository;
import uniauth.jpa.repository.ProjectRoleRepository;
import uniauth.jpa.repository.UserRepository;
import uniauth.service.NotifyService;
import uniauth.service.PermissionQueryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthorityController {

    private final HttpUtil httpUtil;
    private final AppRepository appRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final UserRepository userRepository;
    private final PermissionQueryService permissionQueryService;
    private final NotifyService notifyService;

    @Value("${feishu.app_id}")
    private String appId;

    /**
     * App权限查询 查询用户在此app有哪些权限, 网关在用！！！
     */
    @GetMapping(value = "/permissions/{appName}/user")
    public ApiResponse<AuthResult> getUserAppAuthority(@PathVariable String appName, @RequestParam(value = "user_id") String userId, @RequestParam("user_id_type") String userIdType) {
        var user = switch (userIdType) {
            case "email" -> userRepository.findByEmail(userId).orElseThrow();
            case "open_id" -> userRepository.findByOpenId(userId).orElseThrow();
            default -> throw new IllegalStateException("Unexpected value: " + userIdType);
        };
        var app = appRepository.findByCode(appName).orElseThrow();
        var authResultBuilder = AuthResult.builder();
        if (user.getDefaultProject() != null) {
            var project = user.getDefaultProject();
            var permissions = permissionQueryService.getUserAppProjectPermission(app.getId(), project.getId(), user.getId()).stream().toList();
            authResultBuilder
                    .permissions(permissions)
                    .defaultProjectId(project.getCode());
        } else {
            authResultBuilder.permissions(new ArrayList<>()).defaultProjectId("");
        }
        return new ApiResponse<>(authResultBuilder.build());
    }

    /**
     * 项目角色查询 gitlock在用！！！
     */
    @GetMapping(value = "/gitlock/user/{email:.+}")
    public ApiResponse<Map<String, Object>> getUserAppProjectRoles(
            @PathVariable String email,
            @RequestParam String projectId) {
        var user = userRepository.findByEmail(email).or(() -> userRepository.findByGitEmail(email)).orElseThrow();
        var project = projectRepository.findByCode(projectId).orElseThrow();
        var roles = projectRoleRepository.findUserProjectRoles(user.getUserId(), project.getId());
        var result = new HashMap<String, Object>();
        result.put("user", user);
        result.put("roles", roles);
        return new ApiResponse<>(result);
    }

    /**
     * 获取userAccessToken 知识库订阅在用！！！
     */
//    @GetMapping({"/admin/{user_id}/user_access_token", "/knowagesub/{user_id}/user_access_token"})
//    public ApiResponse<String> retrieveUserAccessToken(@PathVariable String user_id) throws Exception {
//        var user = userRepository.findByUserId(user_id).orElseThrow();
//        var timeMillis = System.currentTimeMillis();
//        if (timeMillis > user.getExpiresIn() && timeMillis < user.getRefreshExpiresIn()) {
//            // 创建请求对象
//            var resp = notifyService.refreshAccessToken(user.getRefreshToken());
//            user.setAccessToken(resp.getAccessToken());
//            user.setExpiresIn(timeMillis + resp.getExpiresIn() * 1000L);
//            user.setRefreshToken(resp.getRefreshToken());
//            user.setRefreshExpiresIn(timeMillis + resp.getRefreshExpiresIn() * 1000L);
//        }
//        Thread.startVirtualThread(() -> userRepository.save(user));
//        return new ApiResponse<>(user.getAccessToken(), "");
//    }

    private record JsapiTicket(int code, String msg, Data data) {
        public record Data(int expire_in, String ticket) {
        }
    }

    /**
     * 获取userAccessToken alabin在用！！！
     */
    @GetMapping("/alabin/user_access_token")
    public ApiResponse<String> retrieveUserAccessTokenAlabin(@RequestParam String user_id, @RequestParam(required = false, defaultValue = "email") String user_id_type) throws Exception {
        var user =  switch (user_id_type) {
            case "email"->userRepository.findByEmail(user_id).orElseThrow();
            default -> throw new IllegalStateException("Unexpected value: " + user_id_type);
        };
        var timeMillis = System.currentTimeMillis();
        if (timeMillis > user.getExpiresIn() && timeMillis < user.getRefreshExpiresIn()) {
            // 创建请求对象
            var resp = notifyService.refreshAccessToken(user.getRefreshToken());
            user.setAccessToken(resp.getAccessToken());
            user.setExpiresIn(timeMillis + resp.getExpiresIn() * 1000L);
            user.setRefreshToken(resp.getRefreshToken());
            user.setRefreshExpiresIn(timeMillis + resp.getRefreshExpiresIn() * 1000L);
            Thread.startVirtualThread(() -> userRepository.save(user));
        }
        return new ApiResponse<>(user.getAccessToken(), "");
    }

    /**
     * 网页组件token查询 返回用户的access_token和js ticket 飞书的网页组件在用！！！
     */
    @GetMapping("/lark/web-components/authentication")
    public ApiResponse<Map<String, Object>> webToken(HttpServletRequest request, @RequestAttribute Auth auth) throws Exception {
        var user = userRepository.findByEmail(auth.getToken().getEmail()).orElseThrow();
        var timeMillis = System.currentTimeMillis();
        if (timeMillis > user.getExpiresIn()) {
            var resp = notifyService.refreshAccessToken(user.getRefreshToken());
            user.setAccessToken(resp.getAccessToken());
            user.setExpiresIn(timeMillis + resp.getExpiresIn() * 1000L);
            user.setRefreshToken(resp.getRefreshToken());
            user.setRefreshExpiresIn(timeMillis + resp.getRefreshExpiresIn() * 1000L);
        }
        if (timeMillis > user.getJsapiTicketExpiresIn()) {
            // jsapi过期
            var jsapiTicket = httpUtil.exchange("https://open.feishu.cn/open-apis/jssdk/ticket/get",
                    HttpMethod.POST, null, JsapiTicket.class, user.getAccessToken());
            user.setJsapiTicket(jsapiTicket.data().ticket());
            user.setJsapiTicketExpiresIn(timeMillis + (jsapiTicket.data().expire_in() - 1000) * 1000L);
        }
        String referer = request.getHeader("referer");
        var resp = generateSignature(user.getJsapiTicket(), referer, user.getAccessToken(), user.getOpenId());
        Thread.startVirtualThread(() -> userRepository.save(user));
        return new ApiResponse<>(resp);
    }

    private Map<String, Object> generateSignature(String jsapiTicket, String url, String accessToken, String openId) {
        var resp = new HashMap<String, Object>();
        var nonceStr = UUID.randomUUID().toString();
        var timeMillis = System.currentTimeMillis();
        resp.put("user_access_token", accessToken);
        resp.put("jsapi_ticket", jsapiTicket);
        resp.put("signature", DigestUtils.sha1Hex(String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%d&url=%s",
                jsapiTicket, nonceStr, timeMillis, url)));
        resp.put("appId", appId);
        resp.put("nonceStr", nonceStr);
        resp.put("timestamp", timeMillis);
        resp.put("openId", openId);
        resp.put("url", url);
        return resp;
    }

}
