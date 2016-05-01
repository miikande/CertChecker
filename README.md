# Certificate Checker
A small Java utility app to check certificates on remote server. Comes with a simple command line interface. In case you plan to use this as an embedded lib, check [CertChecker.java test cases](https://github.com/miikande/CertChecker/blob/master/src/net/pinger/tests/TestCertChecker.java) for some ideas.

Built using wonderful [Apache HttpClient 4.5](https://hc.apache.org/httpcomponents-client-ga/) and [Apache Commons CLI 1.3.1](https://commons.apache.org/proper/commons-cli/) libraries.

Any feedback is welcome and highly appreciated, thanks! :)

## How To
Grab the jar file from dist folder and you're good to go:
```
usage: java -jar CertChecker.jar [options]
 -debug            Switch javax.net.debug SSL debug messages ON
 -help             Displays this help documentation
 -keystore <arg>   Full path to key store file
 -password <arg>   Key store password
 -url <arg>        URL under test
```

## Supported Use Cases
* Checks if given URL can be accessed
* Checks the type of certificate, used by the server
   * Self-signed
   * By trusted certificate authority (CA)
* Checks if local keystore contains a certificate for given URL
* Use _-debug_ flag to get an extra verbose output on SSL processing

# Examples
Here are some examples of using the application.

## Self-signed Certificate
```
$ java -jar CertChecker.jar -url https://self-signed.badssl.com/
Connection was successfully established!
URL: https://self-signed.badssl.com/
Certificate type: Self-signed
```

## CA Signed Certificate
```
$ java -jar CertChecker.jar -url https://www.verisign.com
Connection was successfully established!
URL: https://www.verisign.com
Certificate type: CA signed
```

## Key Store: Match
```
$ java -jar CertChecker.jar -url https://self-signed.badssl.com/ -keystore keystore.jks -password changeme
Connection was successfully established!
URL: https://self-signed.badssl.com/
Certificate type: Self-signed
Key store certification matches with the server!
```

## Key Store: No Cert Found
```
$ java -jar CertChecker.jar -url https://www.verisign.com/ -keystore keystore.jks -password changeme
Connection was successfully established!
URL: https://www.verisign.com/
Certificate type: CA signed
No matching certificate found from key store!
```

## Key Store: Invalid Password
```
$ java -jar CertChecker.jar -url https://www.verisign.com/ -keystore keystore.jks -password invalid
Connection was successfully established!
URL: https://www.verisign.com/
Certificate type: CA signed
Invalid key store password!
```

## Key Store: Key Store Not Found
```
$ java -jar CertChecker.jar -url https://www.verisign.com/ -keystore invalid.jks -password changeme
[ERROR] Can't access given key store file: invalid.jks
```

## Can't Connect Server
```
$ java -jar CertChecker.jar -url https://foo.bar
[ERROR] Can't connect to given URL: https://foo.bar
```
