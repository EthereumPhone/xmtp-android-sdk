# XMTP Android SDK

### Initialize SDK

How to initialize the SDK:

```java
XMTPApi xmtpApi = new XMTPApi(this);
```

### Example code

Some example code on how to interact with the sdk:

```java
// Send a message over XMTP
xmtpApi.sendMessage("Hello. Newest sent from SDK!", "0x2374eFc48c028C98e259a7bBcba336d6acFF103c");

// Get all accounts with which you had a conversation with
xmtpApi.getPeerAccounts().whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
    @Override
    public void accept(ArrayList<String> strings, Throwable throwable) {
        Log.d("First conversation on XMTP", strings.get(0));
    }
});

// Get all messages from a conversation with a specific account
xmtpApi.getMessages("0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete(new BiConsumer<ArrayList<String>, Throwable>() {
    @Override
    public void accept(ArrayList<String> strings, Throwable throwable) {
        Log.d("First message on XMTP", strings.get(0));
    }
});
```

### Supported functions

`void sendMessage(String message, String target)`

To send a message with `message` as the content and `target` as the target address for the message.

`CompletableFuture<ArrayList<String>> getPeerAccounts()`

Returns all accounts with which you had conversations with.

`CompletableFuture<ArrayList<String>> getMessages(String target)`

Returns all messages you had with a specific account.

### Current shortcoming

At the moment when initializing the API, it has an account saved, and there is no way yet to import an account. In the future there should be a `@JavascriptInterface`, which handles the common web3 methods.

