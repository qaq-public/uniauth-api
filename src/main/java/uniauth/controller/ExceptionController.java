package uniauth.controller;

import com.qaq.base.exception.UnAuthorizedException;
import com.qaq.base.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uniauth.service.NotifyService;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionController {

    private final NotifyService notifyService;

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error(e.getMessage(), e);
        notifyService.sendMsgAdmin("uniauth-api: " +  e.getMessage());
        return new ApiResponse<>(-1, null, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:", e);
        return new ApiResponse<>(-1, null, e.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ApiResponse<?> handleUnAuthorizedException(UnAuthorizedException e) {
        log.error("UnAuthorizedException", e);
        return new ApiResponse<>(-1, null, e.getMessage());
    }

}
