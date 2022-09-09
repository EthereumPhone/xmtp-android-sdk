package org.ethereumphone.xmtp_android_sdk;

public interface Signer {
    String signMessage(String msg);
    String getAddress();
}