package com.saisaiwa.tspi.nas.common.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenPair {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long iat;
    private Long expiresIn;
}
