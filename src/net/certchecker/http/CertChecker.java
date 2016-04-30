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
		CA, SELFSIGNED, UNKNOWN
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
				return CertType.SELFSIGNED;
			}
		} catch (Exception e) { /* NO-OP */ }

		// If we reached this point, it means we couldn't determine
		// certificate type the remote end is using...
		return CertType.UNKNOWN;
	}

	public boolean connectUsingKeystore(String url, File keystoreFile,
			String keystorePass) {
		
		boolean ret = false;
		
		try {
			ret = conn.connectWithCert(url, keystoreFile, keystorePass);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}

}
