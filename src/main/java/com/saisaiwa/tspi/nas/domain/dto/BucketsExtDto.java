package com.saisaiwa.tspi.nas.domain.dto;

import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:37
 * @Version：1.0
 */
@Setter
@Getter
public class BucketsExtDto extends Buckets {

    /**
     * 资源名称
     */
    private String resName;

}
