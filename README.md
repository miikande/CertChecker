# Certificate Checker
A really simple Java app to check certificates on remote server. Comes with a command line interface.

Grab the jar file from dist folder and you're good to go:
```
usage: java -jar HttpsPingerApp.jar [options]
 -help             Displays this help documentation
 -keystore <arg>   Full path to keystore file
 -password <arg>   Keystore password
 -port <arg>       Port number of the service (default: 443)
 -url <arg>        URL under test
```

# Supported Use Cases
* Checks if given URL can be accessed
* Checks the type of certificate, used by the server
   * Self-signed
   * By trusted certificate authority (CA)
* Checks if local keystore contains a certificate for given URL
