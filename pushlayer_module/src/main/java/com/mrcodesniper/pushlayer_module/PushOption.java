package com.mrcodesniper.pushlayer_module;

public class PushOption {

    /**
     * 用户信息
     */
    private String userName;
    private String passWord;

    /**
     * 服务器地址
     */
    private String server;
    /**
     * 清除会话
     */
    private boolean cleanSession;

    /**
     * 连接超时 单位S
     */
    private long connectTimeout;

    /**
     * 心跳包检测间隔 单位s
     */
    private int keepAliveInterval;

    /**
     * 推送服务质量类型
     */
    private PushType pushType;

    /**
     * 通信渠道
     */
    private String publishChannel;
    private String responseChannel;

    public String getServer() {
        return server;
    }

    public String getPublishChannel() {
        return publishChannel;
    }

    public void setPublishChannel(String publishChannel) {
        this.publishChannel = publishChannel;
    }

    public String getResponseChannel() {
        return responseChannel;
    }

    public void setResponseChannel(String responseChannel) {
        this.responseChannel = responseChannel;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public PushType getPushType() {
        return pushType;
    }

    public PushOption(String server,String userName, String passWord, boolean cleanSession, long connectTimeout, int keepAliveInterval, PushType pushType, String publishChannel, String responseChannel) {
        this.server=server;
        this.userName = userName;
        this.passWord = passWord;
        this.cleanSession = cleanSession;
        this.connectTimeout = connectTimeout;
        this.keepAliveInterval = keepAliveInterval;
        this.pushType = pushType;
        this.publishChannel = publishChannel;
        this.responseChannel = responseChannel;
    }

    public static class Builder{

        private String userName;
        private String passWord;
        private boolean cleanSession=true;
        private long connectTimeout=10;
        private int keepAliveInterval=20;
        private PushType mType;
        private String publishChannel;
        private String responseChannel;
        private String server;

        public Builder setServer(String server) {
            this.server = server;
            return this;
        }


        public Builder setPublishChannel(String publishChannel) {
            this.publishChannel = publishChannel;
            return this;
        }

        public Builder setResponseChannel(String responseChannel) {
            this.responseChannel = responseChannel;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPushType(PushType mType) {
            this.mType = mType;
            return this;
        }

        public Builder setPassWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder setCleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public PushOption build(){
            return new PushOption(server,userName,passWord,cleanSession,connectTimeout,keepAliveInterval,mType,publishChannel,responseChannel);
        }

    }





}
