package uniauth.controller;

import com.qaq.base.exception.UnAuthorizedException;
import com.qaq.base.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;

    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        log.error(e.getMessage(), e);
        String requestPath = request.getRequestURI();
        String message = String.format("[uniauth-api] Request Path: %s error: %s", requestPath, e.getMessage());
        notifyService.sendMsgAdmin(message);
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
