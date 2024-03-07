package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:25
 * @Version：1.0
 */
@Setter
@Getter
public class UserGroupDetailVo extends UserGroupListVo {

    private List<ResourceItem> resourceItems;

    @Data
    public static class ResourceItem {
        /**
         * 自增主键
         */
        private Long id;

        /**
         * 资源名称
         */
        private String resName;
    }

}
