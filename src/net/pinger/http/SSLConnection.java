package net.pinger.http;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class SSLConnection {
	
	private boolean pDebug = false;
	
	public SSLConnection(boolean debug) {
		pDebug = debug;
		
		// Switch debugging on if so wanted
		if (debug)
			System.setProperty("javax.net.debug", "ssl");
	}
	
	
    public boolean canConnectToAcceptSelfSigned(String url) throws Exception {
    	return canConnectTo(url, true);
    }
    
    public boolean canConnectToDoNotAcceptSelfSigned(String url) throws Exception {
    	return canConnectTo(url, false);
    }

	private boolean canConnectTo(String url, boolean acceptSelfSigned) throws NoSuchAlgorithmException,
			KeyStoreException, KeyManagementException, IOException,
			ClientProtocolException {
		
		boolean ret = false;
    	
    	SSLContextBuilder builder = new SSLContextBuilder();
    	
    	if (acceptSelfSigned) {
    		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    	}
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                sslsf).build();

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        }
        finally {
            response.close();
            ret = true;
        }
        
        return ret;
	}

    /**
     * Calls given URL using provided keystore.
     * 
     * @param url String format of the URL to access
     * @param keystore File path to keystore
     * @param keystorePass String password of the keystore
     * 
     * @return true in case of connection has been established, false otherwise
     * 
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws IOException
     */
	public boolean connectWithCert(String url, File keystore, String keystorePass) 
			throws KeyManagementException, NoSuchAlgorithmException, 
			KeyStoreException, CertificateException, IOException {
		
    	boolean ret = false;
    	
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(keystore, keystorePass.toCharArray())
                .build();
        
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);            
            try {
                HttpEntity entity = response.getEntity();
                log(response.getStatusLine().toString());
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
            ret = true;
        }
        
        return ret;
    }
	
	private void log(String msg) {
		if (pDebug)
			System.out.println(msg);
	}
 
}
