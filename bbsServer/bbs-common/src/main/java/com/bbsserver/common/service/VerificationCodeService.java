package com.bbsserver.common.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import com.bbsserver.common.vo.VerificationCodeVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//图片验证码服务，产生验证码和校验验证码
@Log4j2
@Service
public class VerificationCodeService {

    //储存验证码id和验证码
    private Map<String, String> codeMap = new ConcurrentHashMap<>();

    //产生验证码
    public VerificationCodeVo genCode() {
        //生成图片验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 30, 4, 1);
        //验证码
        String code = lineCaptcha.getCode();
        //验证码图片
        String codeImage = lineCaptcha.getImageBase64Data();
        //验证码id
        String codeId = UUID.fastUUID().toString();

        //储存验证码信息
        codeMap.put(codeId, code);

        log.info("生成验证码："+code);

        return VerificationCodeVo.builder()
                .codeId(codeId)
                .image(codeImage)
                .build();
    }

    //检查验证码是否匹配
    public boolean checkCode(String codeId, String code) {
        //根据验证码id取出验证码
        String codeInfo = codeMap.get(codeId);
        //无效验证码
        if (StringUtils.isBlank(codeInfo)) {
            return false;
        }
        //验证码正确，忽略了大小写
        if (code.equalsIgnoreCase(codeInfo)) {
            return true;
        }
        //验证码不正确
        return false;
    }
}