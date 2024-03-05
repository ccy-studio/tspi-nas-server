package com.saisaiwa.tspi.nas.common.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.saisaiwa.tspi.nas.common.exception.JsonDeserializeException;
import com.saisaiwa.tspi.nas.common.exception.JsonSerializationException;

import java.io.IOException;
import java.io.InputStream;

public class JSONUtil {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        // 反序列化忽略不存在的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        // 序列化属性设置
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // double 序列化自定义（默认使用的科学计数法, 改成BigDecimal的方式）
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Double.class, new DoubleJsonSerializer(Double.class));
        simpleModule.addSerializer(double.class, new DoubleJsonSerializer(Double.class));
        MAPPER.registerModule(simpleModule);
    }

    public static JsonNode read(InputStream in) throws IOException {
        return MAPPER.readTree(in);
    }

    public static String toJSONString(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException(e);
        }
    }

    public static <T> T parseObject(String json, Class<T> clz) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clz);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException(e);
        }
    }

    public static <T> T parseObject(JsonNode jsonNode, Class<T> clz) {
        try {
            return MAPPER.treeToValue(jsonNode, clz);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException(e);
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> type) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializeException(e);
        }
    }

}
