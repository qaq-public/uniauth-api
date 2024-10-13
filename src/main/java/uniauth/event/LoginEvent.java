package uniauth.event;

import com.lark.oapi.service.authen.v1.model.CreateOidcAccessTokenRespBody;
import com.lark.oapi.service.authen.v1.model.GetUserInfoRespBody;
import com.lark.oapi.service.corehr.v1.model.Object;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class LoginEvent extends ApplicationEvent {
    private GetUserInfoRespBody userInfo;
    private CreateOidcAccessTokenRespBody userAccessToken;

    public LoginEvent(GetUserInfoRespBody userInfo, CreateOidcAccessTokenRespBody userAccessToken) {
        super(new Object());
        this.userInfo = userInfo;
        this.userAccessToken = userAccessToken;
    }
}
