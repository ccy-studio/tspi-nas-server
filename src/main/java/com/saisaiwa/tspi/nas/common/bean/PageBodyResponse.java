package com.saisaiwa.tspi.nas.common.bean;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 11:11
 * @Version：1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBodyResponse<T> {

    private long pageSize;

    private long pageNum;

    private long total;

    private List<T> rows;

    public static <T> PageBodyResponse<T> convert(IPage<?> page, List<T> list) {
        return new PageBodyResponse<T>(page.getSize(), page.getCurrent(), page.getTotal(), list);
    }

    public static <T, R> PageBodyResponse<T> convert(IPage<R> page, PageConvert<T, R> fun) {
        return new PageBodyResponse<>(page.getSize(), page.getCurrent(), page.getTotal(), page.getRecords().stream().map(fun::convert).toList());
    }

    @FunctionalInterface
    public interface PageConvert<T, R> {
        /**
         * 转换
         *
         * @param o o
         * @return {@link T}
         */
        T convert(R o);
    }
}
