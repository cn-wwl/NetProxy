package com.wwl.net.udp;

import com.wwl.net.tcp.ProxyTcpPool;
import com.wwl.net.config.TargetConfig;
import com.wwl.net.tcp.ProxyTcpClient;
import com.wwl.net.tcp.TcpClient;
import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author long
 * @date 2023/4/25 15:26
 * @descrption 代理 Udp服务端
 */
@Slf4j
public class ProxyUdpServer implements Runnable{

    private final int port;

    private final TargetConfig targetConfig;

    private DatagramSocket datagramSocket;

    public ProxyUdpServer(int port, TargetConfig targetConfig) {
        this.port = port;
        this.targetConfig = targetConfig;
    }

    @Override
    public void run() {
        try {
            datagramSocket = new DatagramSocket(port);
            log.info("代理服务器已启动,端口："+ port);

            byte[] data = new byte[1024 * 8];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            while(true) {
                datagramSocket.receive(packet);

                byte[] bytes = Arrays.copyOf(packet.getData(), packet.getLength());
                log.info("客户端 => " +  new String(bytes));
                new ProxyUdpClient(targetConfig).sendData(bytes);
            }
        } catch (Exception e) {
            log.error("创建DatagramSocket失败",e);
        }
    }

}
