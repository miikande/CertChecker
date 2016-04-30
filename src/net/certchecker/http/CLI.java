package net.certchecker.http;

import java.io.File;

import net.certchecker.http.CertChecker.CertType;
import net.certchecker.http.CertChecker.KeyStoreConnectionResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
	static Options options = new Options();

	public static void main(String[] args) {
		boolean argumentParseErrors = false;
		File keyStoreFile = null;
		String keyStorePass = null;
		String url = null;
		boolean debug = false;
		boolean canConnect = false;
		
		// create Options object
		
		createOptions(options);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			// Help, anyone?
			if (cmd.hasOption("help")) {
				displayHelp();
				System.exit(0);
			}
			
			// Check the arguments we got from user
			if (cmd.hasOption("url")) {
				url = cmd.getOptionValue("url");
			}
			else {
				System.err.println("[ERROR] URL is required parameter!");
				argumentParseErrors = true;
			}
			
			if (cmd.hasOption("keystore")) {
				String keyStoreFileStr = cmd.getOptionValue("keystore");
				keyStoreFile = new File(keyStoreFileStr);
				
				if (!keyStoreFile.canRead()) {
					System.err.println("[ERROR] Can't access given key store file: " + keyStoreFileStr);
					argumentParseErrors = true;
				}
				
				if (cmd.hasOption("password")) {
					keyStorePass = cmd.getOptionValue("password");
				}
				else {
					System.err.println("[ERROR] Please provide a password for the key store!");
					argumentParseErrors = true;
				}
			}
			
			debug = cmd.hasOption("debug");
			
		} catch (ParseException e) {
			System.err.println("[ERROR] Couldn't parse given arguments: " + e.getMessage());
			argumentParseErrors = true;
			//return;
		}
		
		// Now that we have parsed command line arguments, it's time to
		// do some networking...
		
		if (!argumentParseErrors) {
			CertChecker checker = new CertChecker(debug);
			
			// Start by ensuring the given URL responds to us
			canConnect = checker.canConnect(url);
			if (canConnect) {
				System.out.println("Connection was successfully established!");
				System.out.println("URL: " + url);
				
				// Check used certificate type (CA Vs. Self-signed) 
				CertType certType = checker.getCertificateType(url);
				
				switch (certType) {
				case CA:
					System.out.println("Certificate type: CA signed");
					break;
					
				case SELF_SIGNED:
					System.out.println("Certificate type: Self-signed");
					break;
					
				default:
					System.out.println("Certificate type: UNKNOWN");
					break;
				}
				
				// And finally, we'll check if we can connect to the given URL
				// by using a key store
				if (keyStoreFile != null) {
					KeyStoreConnectionResult res = checker.checkConnectionUsingKeyStore(
							url, keyStoreFile, keyStorePass);
					
					switch (res) {
					case CERT_NOT_FOUND:
						System.out.println("No matching certificate found from key store!");
						break;

					case INVALID_PASSWORD:
						System.out.println("Invalid key store password!");
						break;
						
					case KEY_STORE_NOT_FOUND:
						System.out.println("Key store does not exist: " + keyStoreFile);
						break;
						
					case SUCCESS:
						System.out.println("Key store certification matches with the server!");
						break;
						
					default:
						System.out.println("Unknown result while trying to connect the "
								+ "server using given key store!");
						break;
					}
				}
			}
			else {
				System.err.println("[ERROR] Can't connect to given URL: " + url);
				System.exit(2);
			}
		}
		
	}

	private static void displayHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar CertChecker.jar [options]", options);
	}

	private static void createOptions(Options options) {
		options.addOption("key store", true, "Full path to key store file");
		options.addOption("password", true, "Key store password");
		options.addOption("url", true, "URL under test");
		options.addOption("help", false, "Displays this help documentation");
		options.addOption("debug", false, "Show javax.net.debug SSL debug messages");
	}

}
