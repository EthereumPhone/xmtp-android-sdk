# XMTP Android SDK

### Initialize SDK

How to initialize the SDK:

```kotlin
val xmtpApi = XMTPApi(this)
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
```

### Supported functions

`sendMessage(message: String, target: String): CompletableFuture<String> `

To send a message with `message` as the content and `target` as the target address for the message.

`getPeerAccounts(): CompletableFuture<ArrayList<String>> `

Returns all accounts with which you had conversations with.

`getMessages(target: String): CompletableFuture<ArrayList<String>> `

Returns all messages you had with a specific account.

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
	implementation 'com.github.EthereumPhone:xmtp-android-sdk:0.1.2'
}
```

### Current shortcoming

At the moment when initializing the API, it has an account saved, and there is no way yet to import an account. In the future there should be a `@JavascriptInterface`, which handles the common web3 methods.

