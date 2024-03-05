package com.saisaiwa.tspi.nas.common.util;

import com.saisaiwa.tspi.nas.common.exception.BizException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2023/7/18 15:12
 * @Version：1.0
 */
public class ValidationUtils {

    public static <T> void validate(T object, Validator validator, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            for (ConstraintViolation<T> violation : violations) {
                errorMessages.add(violation.getMessage());
            }
            throw new BizException("参数校验失败: " + errorMessages);
        }
    }

}
