package org.ethereumphone.xmtp_android_sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import org.json.*;

public class XMTPApi {

    private Context context;
    private WebView wv;
    private Signer signer;
    private Map<String, CompletableFuture<ArrayList<String>>> completableFutures;
    private Map<String, CompletableFuture<String>> sentMessages;
    private Map<String, MessageCallback> messageCallbackMap;
    SharedPreferences sharedPreferences;


    public XMTPApi(Context con, Signer signer) {
        context = con;
        this.signer = signer;
        completableFutures = new HashMap<>();
        sentMessages = new HashMap<>();
        messageCallbackMap = new HashMap<>();
        sharedPreferences = con.getSharedPreferences("key", Context.MODE_PRIVATE);

        String content = "";
        try {
            content = getAssetContent(context.getResources().openRawResource(R.raw.init));
        } catch (IOException e) {
            e.printStackTrace();
        }
        wv = new WebView(context);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        wv.getSettings().setAppCacheEnabled(true); //Enable H5 (APPCache) caching
        wv.getSettings().setDatabaseEnabled(true);

        wv.addJavascriptInterface(new AndroidSigner(), "AndroidSigner");


        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>");
        wv.loadDataWithBaseURL("file:///android_res/raw/main_page.html", output.toString(), "text/html", "utf-8", null);
    }

    private String getAssetContent(InputStream filename) throws IOException {
        Scanner s = new Scanner(filename).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    public CompletableFuture<String> sendMessage(String message, String target){
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        webView.getSettings().setAppCacheEnabled(true); //Enable H5 (APPCache) caching
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        webView.addJavascriptInterface(new AndroidSigner(), "AndroidSigner");

        String hash = sha256(message+":"+target+":"+System.currentTimeMillis());
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.getmessages));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        String jsOut = output.toString().replace("%message%", message).replace("%target%", target).replace("%hash%", hash).replace("%WHICH_FUNCTION%", "sendMessage");
        webView.loadDataWithBaseURL("file:///android_asset/index.html", jsOut, "text/html", "utf-8", null);
        this.sentMessages.put(hash, completableFuture);
        return completableFuture;
    }

    public CompletableFuture<ArrayList<String>> getPeerAccounts(){
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        webView.getSettings().setAppCacheEnabled(true); //Enable H5 (APPCache) caching
        webView.getSettings().setDatabaseEnabled(true);

        CompletableFuture<ArrayList<String>> completableFuture = new CompletableFuture<>();
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.getpeeraccounts));
        } catch (IOException e) {
            e.printStackTrace();
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        webView.addJavascriptInterface(new DataReceiver(), "Android");
        webView.addJavascriptInterface(new AndroidSigner(), "AndroidSigner");
        webView.loadDataWithBaseURL("file:///android_asset/index.html", output.toString(), "text/html", "utf-8", null);
        this.completableFutures.put("getPeerAccounts", completableFuture);
        return completableFuture;
    }

    public CompletableFuture<ArrayList<String>> getMessages(String target) {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        webView.getSettings().setAppCacheEnabled(true); //Enable H5 (APPCache) caching
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        CompletableFuture<ArrayList<String>> completableFuture = new CompletableFuture<>();
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.getmessages));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        String jsOut = output.toString().replace("%target%", target).replace("%WHICH_FUNCTION%", "getMessages");
        webView.addJavascriptInterface(new DataReceiver(), "Android");
        webView.addJavascriptInterface(new AndroidSigner(), "AndroidSigner");
        webView.loadDataWithBaseURL("file:///android_asset/index.html", jsOut, "text/html", "utf-8", null);
        this.completableFutures.put("getMessages", completableFuture);
        return completableFuture;
    }

    public void listenMessages(String target, MessageCallback messageCallback) {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true); // Turn on DOM storage
        webView.getSettings().setAppCacheEnabled(true); //Enable H5 (APPCache) caching
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        CompletableFuture<ArrayList<String>> completableFuture = new CompletableFuture<>();
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.listenmessages));
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageCallbackMap.put(target, messageCallback);

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        String jsOut = output.toString().replace("%target%", target);
        webView.addJavascriptInterface(new DataReceiver(), "Android");
        webView.addJavascriptInterface(new AndroidSigner(), "AndroidSigner");
        webView.loadDataWithBaseURL("file:///android_asset/index.html", jsOut, "text/html", "utf-8", null);
    }

    private class DataReceiver {
        @JavascriptInterface
        public void sharePeers(String data) {
            if (completableFutures.get("getPeerAccounts") != null) {
                ArrayList<String> output = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for(int i = 0; i<jsonArray.length();i++){
                        output.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                completableFutures.get("getPeerAccounts").complete(output);
                completableFutures.remove("getPeerAccounts");
            }
        }

        @JavascriptInterface
        public void shareMessages(String data){
            if (completableFutures.get("getMessages") != null) {
                ArrayList<String> output = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    for(int i = 0; i<jsonArray.length();i++){
                        output.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                completableFutures.get("getMessages").complete(output);
                completableFutures.remove("getMessages");
            }
        }

        @JavascriptInterface
        public void sentMessage(String hash){
            if (sentMessages.get(hash) != null) {
                sentMessages.get(hash).complete("");
                sentMessages.remove(hash);
            }
        }

        @JavascriptInterface
        public void listenNewMessage(String target, String senderAddress, String content){
            if(messageCallbackMap.get(target) != null) {
                messageCallbackMap.get(target).newMessage(senderAddress, content);
            }
        }

    }

    private class AndroidSigner {
        @JavascriptInterface
        public String getAddress(){
            return signer.getAddress();
        }

        @JavascriptInterface
        public String signMessage(String message) {
            return signer.signMessage(message);
        }

        @JavascriptInterface
        public void receiveKey(String key){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("key", key);
            editor.apply();
        }

        @JavascriptInterface
        public String getKey(){
            String result = sharedPreferences.getString("key", "null");
            return result;
        }
    }

    public static String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}