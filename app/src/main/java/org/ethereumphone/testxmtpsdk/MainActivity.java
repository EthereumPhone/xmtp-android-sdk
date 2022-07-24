package org.ethereumphone.testxmtpsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.ethereumphone.xmtp_android_sdk.XMTPApi;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XMTPApi xmtpApi = new XMTPApi(this);
        xmtpApi.sendMessage("Hello. Newest sent from SDK!", "0x2374eFc48c028C98e259a7bBcba336d6acFF103c");
        xmtpApi.getPeerAccounts().whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
            @Override
            public void accept(ArrayList<String> strings, Throwable throwable) {
                Log.d("First conversation on XMTP", strings.get(0));
            }
        });
        xmtpApi.getMessages("0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
            @Override
            public void accept(ArrayList<String> strings, Throwable throwable) {
                Log.d("First message on XMTP", strings.get(0));
            }
        });

    }
}