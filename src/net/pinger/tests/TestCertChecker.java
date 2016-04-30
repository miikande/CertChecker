package net.pinger.tests;

import static org.junit.Assert.*;

import java.io.File;

import net.certchecker.http.CertChecker;
import net.certchecker.http.CertChecker.CertType;
import net.certchecker.http.CertChecker.KeyStoreConnectionResult;

import org.junit.Test;

public class TestCertChecker {
	
	private CertChecker checker = new CertChecker(false);
	
	@Test
	public void testCanConnectCASigned() {
		assertTrue(checker.canConnect(INetworkingConstants.URL_CA_SIGNED));
	}
	
	@Test
	public void testCanConnectSelfSigned() {
		assertTrue(checker.canConnect(INetworkingConstants.URL_SELF_SIGNED));
	}

	@Test
	public void testGetCertificateTypeSelfSigned() {
		CertType certType = checker.getCertificateType(INetworkingConstants.URL_SELF_SIGNED);
		
		assertTrue(certType == CertType.SELF_SIGNED);
	}
	
	@Test
	public void testGetCertificateTypeCASigned() {
		CertType certType = checker.getCertificateType(INetworkingConstants.URL_CA_SIGNED);
		
		assertTrue(certType == CertType.CA);
	}
	
	@Test
	public void testConnectUsingKeystoreFileNotFound() {
		KeyStoreConnectionResult res = checker.checkConnectionUsingKeyStore(INetworkingConstants.URL_CA_SIGNED, 
				new File("/invalid/path"), INetworkingConstants.KEY_STORE_PASS);
		
		assertTrue(res == KeyStoreConnectionResult.KEY_STORE_NOT_FOUND);
	}
	
	@Test
	public void testConnectUsingKeystoreInvalidPassword() {
		KeyStoreConnectionResult res = checker.checkConnectionUsingKeyStore(INetworkingConstants.URL_CA_SIGNED, 
				new File(INetworkingConstants.KEY_STORE), "INVALID PASSWORD");
		
		assertTrue(res == KeyStoreConnectionResult.INVALID_PASSWORD);
	}
	
	@Test
	public void testConnectUsingKeystoreCertNotFoundFromKeyStore() {
		KeyStoreConnectionResult res = checker.checkConnectionUsingKeyStore(INetworkingConstants.URL_CA_SIGNED, 
				new File(INetworkingConstants.KEY_STORE), INetworkingConstants.KEY_STORE_PASS);
		
		assertTrue(res == KeyStoreConnectionResult.CERT_NOT_FOUND);
	}

}
