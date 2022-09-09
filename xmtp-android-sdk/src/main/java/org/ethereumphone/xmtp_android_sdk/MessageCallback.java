package org.ethereumphone.xmtp_android_sdk;

public interface MessageCallback {
    void newMessage(String from, String content);
}
