package com.wwl.net.udp;

import com.wwl.net.config.TargetConfig;
import com.wwl.net.tcp.ProxyTcpPool;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

/**
 * @author long
 * @date 2023/4/13 13:56
 * @descrption Udp客户端
 */
@Slf4j
public class ProxyUdpClient {


    private TargetConfig targetConfig;


    public ProxyUdpClient(TargetConfig targetConfig) {
        this.targetConfig = targetConfig;
    }

    public void sendData(byte[] bytes) {
        try {
            DatagramSocket socket = new DatagramSocket();
            // 创建数据报，包含发送的数据信息
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(targetConfig.getIp()), targetConfig.getPort());
            socket.send(packet);
            log.info("代理客户端 => " + new String(bytes));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
