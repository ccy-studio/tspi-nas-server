package com.saisaiwa.tspi.nas.domain.vo;

import com.saisaiwa.tspi.nas.common.bean.TokenPair;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 17:59
 * @Version：1.0
 */
@Data
public class LoginRspVo {

    private UserInfoVo userInfo;

    private TokenPair tokenPair;
}
