package com.wwl.net.tcp;

import com.wwl.net.tcp.ProxyTcpClient;
import com.wwl.net.tcp.TcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author long
 * @date 2023/4/25 10:53
 * @descrption 代理Socket Tcp连接池
 */
@Slf4j
public class ProxyTcpPool {


    /**
     * 客户端列表
     *     String id
     *     TcpClient 实际连接的客户端
     */
    public static ConcurrentHashMap<String, TcpClient> realClients = new ConcurrentHashMap<>();


    /**
     * 代理客户端列表
     *     String id
     *     ProxyTcpClient 代理的客户端
     */
    public static ConcurrentHashMap<String, ProxyTcpClient> proxyClients = new ConcurrentHashMap<>();



    /**
     * 断开重连
     */
    public static void disconnect(String connId){
        if (realClients.get(connId).onLine()){
            realClients.get(connId).close();
            realClients.remove(connId);
        }
        if (proxyClients.get(connId).onLine()){
            proxyClients.get(connId).close();
            proxyClients.remove(connId);
        }
    }

}
