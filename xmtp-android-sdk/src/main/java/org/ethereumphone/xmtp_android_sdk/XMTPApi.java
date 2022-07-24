package org.ethereumphone.xmtp_android_sdk;

import android.content.Context;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import org.json.*;

public class XMTPApi {

    private Context context;
    private WebView wv;
    private Map<String, CompletableFuture<ArrayList<String>>> completableFutures;

    public XMTPApi(Context con) {
        context = con;

        completableFutures = new HashMap<>();

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


    public void sendMessage(String message, String target){
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.sendmessage));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        String jsOut = output.toString().replace("%message%", message).replace("%target%", target);
        this.wv.loadDataWithBaseURL("file:///android_asset/index.html", jsOut, "text/html", "utf-8", null);
    }

    public CompletableFuture<ArrayList<String>> getPeerAccounts(){
        CompletableFuture<ArrayList<String>> completableFuture = new CompletableFuture<>();
        String content = "";
        try {
            content = getAssetContent(this.context.getResources().openRawResource(R.raw.getpeeraccounts));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder output = new StringBuilder();
        output.append("<script type='text/javascript' type='module'>\n");
        output.append(content);
        output.append("</script>\n");
        this.wv.addJavascriptInterface(new DataReceiver(), "Android");
        this.wv.loadDataWithBaseURL("file:///android_asset/index.html", output.toString(), "text/html", "utf-8", null);
        this.completableFutures.put("getPeerAccounts", completableFuture);
        return completableFuture;
    }

    public CompletableFuture<ArrayList<String>> getMessages(String target) {
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
        String jsOut = output.toString().replace("%target%", target);
        this.wv.addJavascriptInterface(new DataReceiver(), "Android");
        this.wv.loadDataWithBaseURL("file:///android_asset/index.html", jsOut, "text/html", "utf-8", null);
        this.completableFutures.put("getMessages", completableFuture);
        return completableFuture;
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
    }
}

