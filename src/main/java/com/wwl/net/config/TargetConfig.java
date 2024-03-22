package com.wwl.net.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author long
 * @date 2023/4/25 10:45
 * @descrption 目标服务器 配置类
 */
@Configuration
@Getter
public class TargetConfig {

    @Value("${target.ip}")
    private String ip;

    @Value("${target.port}")
    private int port;

}
