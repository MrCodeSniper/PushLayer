package com.mrcodesniper.pushlayer_module;

public enum PushType {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    ONLY_ONCE(2);

    int value;

    PushType(int value) {
        this.value = value;
    }
}
