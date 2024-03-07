package com.saisaiwa.tspi.nas.common.bean;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 10:56
 * @Version：1.0
 */
@Setter
@Getter
public class BasePageReq extends Page {

    private String query;

}
