package com.bbsserver.common.vo;

import lombok.Builder;
import lombok.Data;


//响应验证码请求返回的数据，验证码id和验证图片
@Data
@Builder
public class VerificationCodeVo {

    private String codeId;

    private String image;
}