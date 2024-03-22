package com.wwl.net.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author long
 * @date 2023/4/13 13:56
 * @descrption Tcp客户端
 */
@Slf4j
public class TcpClient implements Runnable {

    private final String connId;
    private final Socket socket;

    DataInputStream dis = null;
    DataOutputStream dos = null;

    public TcpClient(String connId, Socket socket) {
        this.connId = connId;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            //获取输入流
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            getInputStream();
        } catch (IOException e) {
            try {
                if (!socket.isClosed()){
                    socket.close();
                }
            } catch (IOException ioException) {
               log.error("关闭socket失败",ioException);
            }
            System.out.println("客户端" + socket.getRemoteSocketAddress().toString() + "断开连接");
            ProxyTcpPool.disconnect(connId);
        }
    }

    private void getInputStream() throws IOException {

        byte[] bytes = new byte[1024 * 8];
        int length;
        while ((length = dis.read(bytes)) != -1) {
            byte[] bytes1 = Arrays.copyOf(bytes, length);
            String str = new String(bytes1);
            log.info("客户端 => " + str);
            ProxyTcpPool.proxyClients.get(connId).sendData(bytes1);
        }
    }


    public void sendData(byte[] bytes) {
        try {
            dos.write(bytes);

            log.info("代理服务器 => " + new String(bytes));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
            dis.close();
            dos.close();
            socket.close();
            log.info("代理服务器" + socket.getLocalAddress().toString() + "断开连接");
        } catch (IOException e) {
            log.error("关闭socket异常",e);
        }
    }


    public boolean onLine() {
        return !socket.isClosed();
    }

}
