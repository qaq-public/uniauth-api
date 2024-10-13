package uniauth.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uniauth.event.LoginEvent;
import uniauth.exception.ErrorMessageException;
import uniauth.service.sso.LarkWebSSOService;
import uniauth.util.CookieUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/web")
@Controller
public class WebLoginController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final LarkWebSSOService ssoService;

    @RequestMapping(value = "/login")
    public void login(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam(value = "next") String next) throws URISyntaxException, IOException {
        response.sendRedirect(ssoService.authorize(LarkWebSSOService.getNextUrl(request)));
    }

    @GetMapping(value = "/login/callback")
    public void loginCallback(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestParam("next") String next,
                              @RequestParam("code") String code) throws IOException {
        log.debug("The login_callback code: {}", code);
        if (code.isEmpty()) {
            log.error("The code check wrong");
            throw new ErrorMessageException("回调Code错误");
        }
        var createOidcAccessTokenRespBody = ssoService.code2accessToken(code);
        var getUserInfoRespBody = ssoService.accessToken2user(createOidcAccessTokenRespBody.getAccessToken());
        var jwt = ssoService.user2JWT(getUserInfoRespBody);
        CookieUtil.setJWTCookie(response, jwt, request.getHeader("Host").split(":")[0]);
        String redirectUrl = "/";
        try {
            redirectUrl = new String(Base64.getUrlDecoder().decode(next));
        } catch (Exception ignored) {
        }
        response.sendRedirect(redirectUrl);
        applicationEventPublisher.publishEvent(new LoginEvent(getUserInfoRespBody, createOidcAccessTokenRespBody));
    }

    @GetMapping(value = "/logout")
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       @RequestParam(value = "next", defaultValue = "/") String next) throws IOException {
        String host = request.getHeader("Host");
        host = host.split(":")[0];
        CookieUtil.delJWTCookie(response, host);
        log.debug("the redirectUrl is : {}", next);
        response.sendRedirect(next);
    }

}
