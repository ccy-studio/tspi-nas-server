package com.saisaiwa.tspi.nas.common.exception;

import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WebExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionHandler.class);


    @ExceptionHandler(BizException.class)
    public BaseResponse<Void> bizException(BizException ex) {
        LOGGER.error("Business exceptions: ", ex);
        return BaseResponse.fail(ex.getRespCode());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    private BaseResponse<Void> duplicateKeyException(DuplicateKeyException exception) {
        LOGGER.error("Duplicated data: ", exception);
        BaseResponse<Void> response = BaseResponse.fail(RespCode.ERROR);
        response.setMsg("数据重复");
        return response;
    }

    /**
     * 业务代码检查参数错误
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse<Void> illegalArgumentException(BizException ex) {
        LOGGER.error("Parameter error: ", ex);
        return BaseResponse.fail(RespCode.INVALID_PARAMS);
    }


    /**
     * {@code @Validated} 校验参数错误
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<String> constraintViolationException(ConstraintViolationException e) {
        LOGGER.error("Request parameters error:", e);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        String msg = String.join(";", collect);
        return BaseResponse.fail(RespCode.INVALID_PARAMS.getCode(), msg, null);

    }

    /**
     * Spring 数据绑定参数错误
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        LOGGER.error("Request Parameters error: ", ex);
        BaseResponse<Void> response;
        String msg = RespCode.INVALID_PARAMS.getMsg();
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fe) {
                details.add(fe.getField() + ": " + error.getDefaultMessage());
            } else {
                details.add(error.getDefaultMessage());
            }
        }
        if (!details.isEmpty()) {
            msg = String.join(",", details);
        }
        response = BaseResponse.fail(RespCode.INVALID_PARAMS.getCode(), msg, null);
        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatusCode status, WebRequest request) {
        LOGGER.error("No handler found: ", ex);
        BaseResponse<Void> response = BaseResponse.fail(RespCode.BAD_REQUEST);
        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        LOGGER.error("HttpMessageNotReadableException:", ex);
        BaseResponse<Object> fail = BaseResponse.fail(RespCode.BAD_REQUEST.getCode(), ex.getMessage(), null);
        return new ResponseEntity<>(fail, status);
    }

    @ExceptionHandler({Exception.class})
    public BaseResponse<Void> handleException(Exception ex) {
        LOGGER.error("System exception: ", ex);
        return BaseResponse.fail(RespCode.ERROR);
    }

}
