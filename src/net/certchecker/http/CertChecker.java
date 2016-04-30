package net.certchecker.http;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CertChecker {
	
	private SSLConnection conn;
	
	public static enum CertType {
		CA, SELF_SIGNED, UNKNOWN
	}
	
	public static enum KeyStoreConnectionResult {
		KEY_STORE_NOT_FOUND,
		INVALID_PASSWORD,
		CERT_NOT_FOUND,
		SUCCESS
	}
	
	public CertChecker(boolean debug) {
		conn = new SSLConnection(debug);
	}
	
	public CertChecker() {
		conn = new SSLConnection(false);
	}

	public boolean canConnect(String url) {
		boolean canConnect = false;
		
		try {
			canConnect = conn.canConnectToAcceptSelfSigned(url);
		} catch (Exception e) { /* NO-OP */ }
		
		return canConnect;
	}

	public CertType getCertificateType(String url) {
		
		// First we'll check for CA signed certification
		try {
			if (conn.canConnectToDoNotAcceptSelfSigned(url)) {
				return CertType.CA;
			}
		} catch (Exception e) { /* NO-OP */ }
		
		// Then self-signed
		try {
			if (conn.canConnectToAcceptSelfSigned(url)) {
				return CertType.SELF_SIGNED;
			}
		} catch (Exception e) { /* NO-OP */ }

		// If we reached this point, it means we couldn't determine
		// certificate type the remote end is using...
		return CertType.UNKNOWN;
	}

	public KeyStoreConnectionResult checkConnectionUsingKeyStore(String url, File keyStoreFile,
			String keyStorePass) {
		
		try {
			conn.connectWithCert(url, keyStoreFile, keyStorePass);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			if (e.getMessage().contains("password"))
				return KeyStoreConnectionResult.INVALID_PASSWORD;
			
			if (e.getMessage().contains("unable to find valid certification"))
				return KeyStoreConnectionResult.CERT_NOT_FOUND;
				
			return KeyStoreConnectionResult.KEY_STORE_NOT_FOUND;
		}
		
		return KeyStoreConnectionResult.SUCCESS;
	}

}
