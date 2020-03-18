package com.mrcodesniper.design;

/**
 * 每次请求拿的数据包都存在缓存队列中 需要的去里面取不直接跟服务器连接
 */
public  class PingPackage {


    public static final String PACKET_NAME="PINGREQ";

    private byte type;//一个字节大小以内表示类型

    private int messageId;

    private boolean duplicated;//去除

    public byte[] getPayload()  {
        return new byte[0];
    }

    public byte[] getHeader(){
        return null;
    }


}
