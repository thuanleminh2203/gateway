package com.venesa.gateway.exception;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.mobio.request.ListBookingRq;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.gateway.component.WrapperResponseData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class ExceptionHandleGlobal {
    private final WrapperResponseData wrapperResponse;

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleError404(Exception e) {
        return wrapperResponse.error(
                new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleErrorRabbitMq(Exception e) {
        log.error("=====err=========" + e.getMessage());
        return wrapperResponse.error(
                new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleErrorNullPointer(Exception e) {
        log.error("=====err=========" + e.getMessage());
        return wrapperResponse.error(
                new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public void handleRabbitMQException(BusinessException e) {
        log.error("=====err=========" + e.getMessage());
    }

}
