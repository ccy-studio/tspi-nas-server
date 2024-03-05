package com.saisaiwa.tspi.nas.config;

import com.saisaiwa.tspi.nas.common.util.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice
public class WebRequestLogAdapter extends RequestBodyAdviceAdapter implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private HttpServletRequest httpServletRequest;

    /**
     * GET 请求参数打印
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name())) {
            logger.info("REQ URI: {} Params: {}", request.getRequestURI(), JSONUtil.toJSONString((request.getParameterMap())));
        }
        return true;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * POST 请求参数打印
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        logger.info("REQ URI: {} Body: {}", httpServletRequest.getRequestURI(), JSONUtil.toJSONString(body));
        return body;
    }
}
