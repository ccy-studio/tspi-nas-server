package com.saisaiwa.tspi.nas.domain.file;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @date: 2024/03/13 16:40
 * @author: saisiawa
 **/
@Setter
@Getter
public class FBaseEntity {

    /**
     * 桶ID
     */
    private Long bucketId;

    /**
     * 对象ID
     */
    private Long objectId;

}
