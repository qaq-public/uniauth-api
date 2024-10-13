package uniauth.service;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.application.v6.enums.MessageTypeEnum;
import com.lark.oapi.service.authen.v1.model.CreateOidcRefreshAccessTokenReq;
import com.lark.oapi.service.authen.v1.model.CreateOidcRefreshAccessTokenReqBody;
import com.lark.oapi.service.authen.v1.model.CreateOidcRefreshAccessTokenRespBody;
import com.lark.oapi.service.contact.v3.model.GetUserReq;
import com.lark.oapi.service.contact.v3.model.GetUserResp;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.ext.MessageTemplate;
import com.lark.oapi.service.im.v1.model.ext.MessageTemplateData;
import com.lark.oapi.service.im.v1.model.ext.MessageText;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uniauth.exception.ErrorMessageException;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotifyService {

    @Value("${qaq.admin}")
    private String qaqAdmin;
    private final Client client;

    @SneakyThrows
    public void sendCardAdmin(String templateId, Map<String, Object> templateVariable) {

        var data = MessageTemplate.newBuilder().data(
                        MessageTemplateData.newBuilder().templateId(templateId).templateVariable(templateVariable).build())
                .build();
        var resp = client.im().message()
                .create(CreateMessageReq.newBuilder().receiveIdType(ReceiveIdTypeEnum.EMAIL.getValue())
                        .receiveIdType(CreateMessageReceiveIdTypeEnum.EMAIL)
                        .createMessageReqBody(CreateMessageReqBody.newBuilder().content(data)
                                .receiveId(qaqAdmin)
                                .msgType(MessageTypeEnum.INTERACTIVE.getValue()).build())
                        .build());
        if (resp.getCode() != 0) {
            log.error("sendCardAdmin code: {}, msg: {}, err: {}", resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError()));
        }
    }

    @SneakyThrows
    public void sendCard(String templateId, Map<String, Object> templateVariable, String receiveId, String receiveType) {

        var data = MessageTemplate.newBuilder().data(
                        MessageTemplateData.newBuilder().templateId(templateId).templateVariable(templateVariable).build())
                .build();
        var resp = client.im().message()
                .create(CreateMessageReq.newBuilder().receiveIdType(receiveType)
                        .createMessageReqBody(CreateMessageReqBody.newBuilder().content(data)
                                .receiveId(receiveId)
                                .msgType(MessageTypeEnum.INTERACTIVE.getValue()).build())
                        .build());
        if (resp.getCode() != 0) {
            log.error("sendCard code: {}, msg: {}, err: {}", resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError()));
        }
    }

    public void sendCard(String templateId, Map<String, Object> templateVariable, List<String> receiveIds, String receiveType) {
        var data = MessageTemplate.newBuilder().data(
                        MessageTemplateData.newBuilder().templateId(templateId).templateVariable(templateVariable).build())
                .build();
        receiveIds.forEach(receiveId -> {
            try {
            var resp = client.im().message()
                    .create(CreateMessageReq.newBuilder().receiveIdType(receiveType)
                            .createMessageReqBody(CreateMessageReqBody.newBuilder().content(data)
                                    .receiveId(receiveId)
                                    .msgType(MessageTypeEnum.INTERACTIVE.getValue()).build())
                            .build());
            if (resp.getCode() != 0) {
                log.error("sendCard code: {}, msg: {}, err: {}", resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError()));
            }}
            catch (Exception e) {
                log.error("sendCard error: {}", e.getMessage());
            }
        });
    }

    public CreateOidcRefreshAccessTokenRespBody refreshAccessToken(String refreshToken) throws Exception {
        log.debug("refresh token: {}", refreshToken);
        var req = CreateOidcRefreshAccessTokenReq.newBuilder()
                .createOidcRefreshAccessTokenReqBody(CreateOidcRefreshAccessTokenReqBody.newBuilder()
                        .grantType("refresh_token")
                        .refreshToken(refreshToken)
                        .build())
                .build();

        // 发起请求
        var resp = client.authen().oidcRefreshAccessToken().create(req);

        // 处理服务端错误
        if (!resp.success()) {
            log.error("refreshAccessToken code: {}, msg: {}, err: {}", resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError()));
            throw new ErrorMessageException(resp.getMsg());
        }
        return resp.getData();
    }

    @SneakyThrows
    public com.lark.oapi.service.contact.v3.model.User retrieveUser(String userId, String userIdType) {
        // 创建请求对象
        var req = GetUserReq.newBuilder()
                .userId(userId)
                .userIdType(userIdType)
                .build();

        // 发起请求
        GetUserResp resp = client.contact().user().get(req);

        // 处理服务端错误
        if (!resp.success()) {
            log.error("retrieveUser code: {}, msg: {}, reqId: {}", resp.getCode(), resp.getMsg(), resp.getRequestId());
            return null;
        }

        // 业务数据处理
        return resp.getData().getUser();
    }

    @SneakyThrows
    public void sendMsgAdmin(String msg) {
        var content = MessageText.newBuilder().text(msg).build();
        var resp = client.im().message().create(CreateMessageReq.newBuilder()
                .receiveIdType(CreateMessageReceiveIdTypeEnum.EMAIL)
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(qaqAdmin)
                        .content(content)
                        .msgType(com.lark.oapi.service.im.v1.enums.MsgTypeEnum.MSG_TYPE_TEXT
                                .getValue())
                        .build())
                .build());
        if (resp.getCode() != 0) {
            log.error("sendMsgAdmin code: {}, msg: {}, err: {}", resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError()));
        }
    }
}
