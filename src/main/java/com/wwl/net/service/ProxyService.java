package com.wwl.net.service;

import com.wwl.net.base.ProtocolType;
import com.wwl.net.config.ProxyConfig;
import com.wwl.net.config.TargetConfig;
import com.wwl.net.tcp.ProxyTcpServer;
import com.wwl.net.udp.ProxyUdpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author long
 * @date 2023/4/25 11:03
 * @descrption 代理服务器 服务
 */
@Service
@Slf4j
public class ProxyService implements ApplicationRunner {

    @Autowired
    private ProxyConfig proxyConfig;

    @Autowired
    private TargetConfig targetConfig;

    @Override
    public void run(ApplicationArguments args) {
        ProtocolType protocol = ProtocolType.valueType(proxyConfig.getProtocol());
        switch (Objects.requireNonNull(protocol)) {
            case Tcp:
                createProxyTcpServer();
                break;
            case Udp:
                createProxyUdpServer();
                break;
            default:
                log.info("不支持该协议类型："+proxyConfig.getProtocol());
                break;
        }
    }

    private void createProxyTcpServer(){
        // 创建代理Tcp服务端
        ProxyTcpServer proxyTcpServer = new ProxyTcpServer(proxyConfig.getPort(), targetConfig);
        new Thread(proxyTcpServer).start();
    }

    private void createProxyUdpServer(){
        // 创建代理Udp服务端
        ProxyUdpServer proxyUdpServer = new ProxyUdpServer(proxyConfig.getPort(), targetConfig);
        new Thread(proxyUdpServer).start();
    }
}
