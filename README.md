# XMTP Android SDK

### Initialize SDK

How to initialize the SDK:

```kotlin
val xmtpApi = XMTPApi(this, signer)
```

### Example code

Some example code on how to interact with the sdk:

```kotlin
// Send a message over XMTP
xmtpApi.sendMessage("Hey!", "0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete { s, throwable ->
	Log.d("First message on TEST", s)
}

// Get all accounts with which you had a conversation with
xmtpApi.peerAccounts.whenComplete { arrayList, throwable ->
	Log.d("First conversation on TEST", arrayList[0]!!)
}

// Get all messages from a conversation with a specific account
xmtpApi.getMessages("0x2374eFc48c028C98e259a7bBcba336d6acFF103c").whenComplete { arrayList, throwable ->
	Log.d("First message on TEST", arrayList[0]!!)
}

// Listen for new messages
xmtpApi.listenMessages("0x2374eFc48c028C98e259a7bBcba336d6acFF103c", messageCallback)
```

### Supported functions

`sendMessage(message: String, target: String): CompletableFuture<String> `

To send a message with `message` as the content and `target` as the target address for the message.

`getPeerAccounts(): CompletableFuture<ArrayList<String>> `

Returns all accounts with which you had conversations with.

`getMessages(target: String): CompletableFuture<ArrayList<String>> `

Returns all messages you had with a specific account.

`listenMessages(target: String, messageCallback: MessageCallback)`

Listens to new message from `target` address and calls the `messageCallback` if a new one arrives.

### How to add the SDK

Go to your root `build.gradle` or `settings.gradle`, and add this:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Then add the dependency:

```
dependencies {
	implementation 'com.github.EthereumPhone:xmtp-android-sdk:0.1.3'
}
```

### Signer

To be able to send messages from your own account also receive them, you need to implement the Signer interface:

```java
public interface Signer {
    String signMessage(String msg);
    String getAddress();
}
```

I provided an example using [WalletConnectKit](https://github.com/pink-room/walletconnectkit-android) in the example app attached to this repo. If you have MetaMask installed it is really easy to write the Signer Implementation.

### MessageCallback

To use the `listenMessages` function, you need to provide an implementation of the MessageCallback interface:

```java
public interface MessageCallback {
    void newMessage(String from, String content);
}
```

The function `newMessage` will be called when a new message from a specific address is received.
