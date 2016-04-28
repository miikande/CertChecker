# HttpsPinger
A small command line application to ping HTTPS urls. Works both with or without relying on client side keystore.

```
usage: java -jar HttpsPingerApp.jar [options]
 -help             Displays this help documentation
 -keystore <arg>   Full path to keystore file
 -password <arg>   Keystore password
 -port <arg>       Port number of the service (default: 443)
 -url <arg>        URL under test
```

Use case for this app is to be able to check if certificate, stored in a key store, still matches with the remote server.
