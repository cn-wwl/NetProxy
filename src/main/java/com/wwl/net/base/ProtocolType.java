package com.wwl.net.base;

/**
 * @author long
 * @date 2023/4/25 13:07
 * @descrption 协议类型
 */
public enum ProtocolType {

    /**
     * Tcp
     */
    Tcp("tcp"),

    /**
     * Udp
     */
    Udp("udp")

    ;
    private final String value;
    ProtocolType(String value){
        this.value = value;
    }

    public static ProtocolType valueType(String value){
        for (ProtocolType type : ProtocolType.values()) {
            if (type.value.equals(value)){
                return type;
            }
        }
        return null;
    }

}
