package com.wwl.net.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author long
 * @date 2023/4/13 16:57
 * @descrption 代理Tcp客户端
 */
@Slf4j
public class ProxyTcpClient implements Runnable {

    private final String connId;

    private final String ip;
    private final int port;

    private Socket socket;

    DataInputStream dis = null;
    DataOutputStream dos = null;

    public ProxyTcpClient(String connId, String ip, int port) {
        this.connId = connId;
        this.ip = ip;
        this.port = port;
    }


    private void initSocket() {

        try {
            socket = new Socket(ip, port);
            log.info(String.format("连接目标服务器成功：[%s:%d]", ip, port));

        } catch (Exception ex) {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ioException) {
                log.error("关闭连接失败", ioException);
            }

            log.error(String.format("连接目标服务器失败：[%s:%d]", ip, port), ex);
        }
    }


    @Override
    public void run() {
        try {
            initSocket();
            //获取输入、输出流
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            getInputStream();
        } catch (IOException e) {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ex) {
                log.error("关闭连接失败", ex);
            }
            assert socket != null;
            log.info("目标服务器" + socket.getRemoteSocketAddress().toString() + "断开连接");
            ProxyTcpPool.disconnect(connId);
        }
    }

    public void close() {
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
            dis.close();
            dos.close();
            socket.close();
            log.info("代理客户端" + socket.getLocalAddress().toString() + "断开连接");
        } catch (IOException e) {
          log.error("关闭socket异常",e);
        }
    }

    private void getInputStream() throws IOException {
        while (onLine()) {

            byte[] bytes = new byte[1024 * 8];
            int length;
            while ((length = dis.read(bytes)) != -1) {
                byte[] bytes1 = Arrays.copyOf(bytes, length);
                String str = new String(bytes1);
                log.info("目标服务器 => " + str);

                ProxyTcpPool.realClients.get(connId).sendData(bytes1);
            }
        }
    }

    public void sendData(String content) {
        try {
            dos.write(content.getBytes());
            log.info("代理客户端 => " + content);
        } catch (Exception ex) {
            log.error("发送数据失败",ex);
        }
    }

    public void sendData(byte[] bytes) {
        try {
            dos.write(bytes);
            log.info("代理客户端 => " + new String(bytes));
        } catch (Exception ex) {
            log.error("发送数据失败",ex);
        }
    }

    public boolean onLine() {
        return !socket.isClosed();
    }

}
