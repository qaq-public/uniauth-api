package uniauth.service.sso;

import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.authen.v1.model.*;
import com.qaq.base.component.JWTGeneratorComponent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uniauth.exception.ErrorMessageException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Service
public class LarkWebSSOService {

    private final Client client;
    private final JWTGeneratorComponent jwtGeneratorComponent;

    @Value("${feishu.app_id}")
    private String appId;

    public String authorize(String redirectUri) throws URISyntaxException {
        UriComponentsBuilder uri = UriComponentsBuilder.fromUri(new URI("https://open.feishu.cn/open-apis/authen/v1/authorize"))
                .queryParam("app_id", appId)
                .queryParam("redirect_uri", redirectUri);
//                .queryParam("scope", "wiki:wiki bitable:app");
        return uri.build().toUriString();
    }

    @SneakyThrows
    public CreateOidcAccessTokenRespBody code2accessToken(String code) {
        // 创建请求对象
		CreateOidcAccessTokenReq req = CreateOidcAccessTokenReq.newBuilder()
			.createOidcAccessTokenReqBody(CreateOidcAccessTokenReqBody.newBuilder()
				.grantType("authorization_code")
				.code(code)
				.build())
			.build();

		// 发起请求
		CreateOidcAccessTokenResp resp = client.authen().oidcAccessToken().create(req);

		// 处理服务端错误
		if(!resp.success()) {
			log.error("code: {},msg: {},reqId: {}", resp.getCode(), resp.getMsg(), resp.getRequestId());
			throw new ErrorMessageException(resp.getMsg());
		}
        return resp.getData();
    }

    @SneakyThrows
    public GetUserInfoRespBody accessToken2user(String access_token)  {
        // 创建请求对象
		// 发起请求
		GetUserInfoResp resp = client.authen().userInfo().get(RequestOptions.newBuilder()
        .userAccessToken(access_token)
        .build());

		// 处理服务端错误
		if(!resp.success()) {
			log.error("accessToken2user code: {},msg: {},reqId: {}", resp.getCode(), resp.getMsg(), resp.getRequestId());
			throw new ErrorMessageException(resp.getMsg());
		}
        return resp.getData();
    }

    public String user2JWT(GetUserInfoRespBody user) {
        return jwtGeneratorComponent.generate(user);
    }

    static public String getNextUrl(HttpServletRequest request) {
        UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .path("/web")
                .path("/login/callback")
                .queryParam("next", request.getParameter("next"));
        if (request.getServerPort() != 80 && request.getServerPort()!=443) {
            uri.port(request.getServerPort());
        }
        return java.net.URLEncoder.encode(uri.build().toUriString(), StandardCharsets.UTF_8);
    }
}
