package net.pinger.tests;

import static org.junit.Assert.*;

import java.io.File;

import net.pinger.http.SSLConnection;

import org.junit.Test;

import sun.security.validator.ValidatorException;

public class TestSSLConnection {
	
	String url = "https://self-signed.badssl.com/";
	String keyStoreFile = "/Users/miikka/git/james/keystore.jks";
	String pass = "changeme";

	@Test
	public void testConnectNoCert() {
		SSLConnection conn = new SSLConnection(false);
		boolean ret = false;
		
		try {
			ret = conn.connectNoCert(null);
		} catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}
		
		assertTrue(ret);
	}

	
	@Test
	public void testConnectWithCert() {
		SSLConnection conn = new SSLConnection(false);
		boolean ret = false;
		try {
			ret = conn.connectWithCert(url, new File(keyStoreFile), pass);
		}
		catch (ValidatorException e) {
			System.err.println("[ERROR] Can't find valid certificate for given URL!");
		}
		catch (Exception e) {
			System.out.println("[ERROR] " + e.getMessage());
		}
		
		assertTrue(ret);
	}
}
