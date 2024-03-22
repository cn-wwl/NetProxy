package com.wwl.net.tcp;

import com.wwl.net.config.TargetConfig;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * @author long
 * @date 2023/4/25 11:18
 * @descrption 代理 Tcp服务端
 */
@Slf4j
public class ProxyTcpServer implements Runnable{

    private final int port;

    private final TargetConfig targetConfig;

    private ServerSocket serverSocket;

    public ProxyTcpServer(int port, TargetConfig targetConfig) {
        this.port = port;
        this.targetConfig = targetConfig;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            log.info("代理服务器已启动,端口："+ port);
            while (true) {
                // 接收客户端连接的socket对象
                Socket connection;
                try {
                    // 接收客户端传过来的数据，会阻塞
                    connection = serverSocket.accept();

                    String connId = UUID.randomUUID().toString().replace("-","");
                    addTcpClientConn(connId,connection);
                    addProxyTcpClientConn(connId);
                } catch (Exception e) {
                   log.error("ProxyTcpServer",e);
                }
            }
        } catch (Exception e) {
            log.error("创建socketServer失败",e);
        }
    }

    private void addTcpClientConn(String connId,Socket connection){


        TcpClient tcpClient = new TcpClient(connId,connection);
        new Thread(tcpClient).start();
        ProxyTcpPool.realClients.put(connId, tcpClient);
    }

    private void addProxyTcpClientConn(String connId){
        ProxyTcpClient proxyTcpClient = new ProxyTcpClient(connId,targetConfig.getIp(), targetConfig.getPort());
        new Thread(proxyTcpClient).start();
        ProxyTcpPool.proxyClients.put(connId,proxyTcpClient);
    }

}
