package org.ethereumphone.testxmtpsdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.ethereumphone.xmtp_android_sdk.XMTPApi;
import org.ethereumphone.xmtp_android_sdk.Signer;
import org.walletconnect.Session;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpType;


import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import dev.pinkroom.walletconnectkit.WalletConnectButton;
import dev.pinkroom.walletconnectkit.WalletConnectKit;
import dev.pinkroom.walletconnectkit.WalletConnectKitConfig;

import kotlin.Unit;
import kotlin.coroutines.*;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private WalletConnectKit walletConnectKit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context con = this;
        walletConnectKit = new WalletConnectKit.Builder(new WalletConnectKitConfig(this, "https://bridge.walletconnect.org", "https://ethereumphone.org", "XMTP Test", "Test", new ArrayList<>())).build();

        WalletConnectButton walletConnectButton = (WalletConnectButton) findViewById(R.id.walletConnectButton);
        walletConnectButton.start(walletConnectKit, new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {

                //Connected to wallet
                SignerImpl signer = new SignerImpl(walletConnectKit);

                XMTPApi xmtpApi = new XMTPApi(con, signer);

/**
                xmtpApi.getMessages("0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
                    @Override
                    public void accept(ArrayList<String> strings, Throwable throwable) {
                        Log.d("First message on XMTP", strings.get(0));
                    }
                });

                xmtpApi.getPeerAccounts().whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
                    @Override
                    public void accept(ArrayList<String> strings, Throwable throwable) {
                        Log.d("First conversation on XMTP", strings.get(0));
                    }
                });
 */
                xmtpApi.sendMessage("Hey, howie doin", "0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete(new BiConsumer<String, Throwable>() {
                    @Override
                    public void accept(String s, Throwable throwable) {
                        Log.d("Message sent! on XMTP", s);
                    }
                });

                return null;
            }
        });

    }
}

class SignerImpl implements Signer {
    WalletConnectKit walletConnectKit;
    HashMap<String, String> hashMap;
    public SignerImpl(WalletConnectKit walletConnectKit) {
        this.walletConnectKit = walletConnectKit;
        hashMap = new HashMap<>();
    }

    @Override
    public String signMessage(String msg) {
        hashMap.put(msg, "");
        Object out = walletConnectKit.personalSign(msg, new Continuation<Session.MethodCall.Response>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
                Session.MethodCall.Response response = (Session.MethodCall.Response) o;
                hashMap.replace(msg, response.getResult().toString());
            }
        });
        while(hashMap.get(msg).equals("")){
            System.out.println("not done yet: "+hashMap.get(msg));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return hashMap.get(msg);
    }

    @Override
    public String getAddress() {
        return walletConnectKit.getAddress();
    }
}