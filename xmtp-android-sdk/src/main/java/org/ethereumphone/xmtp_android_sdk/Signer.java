package org.ethereumphone.xmtp_android_sdk;

interface Signer {
    String signMessage(String msg);
    String getAddress();
}