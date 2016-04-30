package net.certchecker.http;

import java.io.File;

import net.certchecker.http.CertChecker.CertType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import sun.security.validator.ValidatorException;

public class CLI {
	static Options options = new Options();

	public static void main(String[] args) {
		boolean argumentParseErrors = false;
		File keystoreFile = null;
		String keystorePass = null;
		String url = null;
		int port = 443;
		boolean debug = false;
		boolean canConnect = false;
		boolean canConnectWithKeystore = false;
		
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
				String keystoreFileStr = cmd.getOptionValue("keystore");
				keystoreFile = new File(keystoreFileStr);
				
				if (!keystoreFile.canRead()) {
					System.err.println("[ERROR] Can't access given keystore file: " + keystoreFileStr);
					argumentParseErrors = true;
				}
				
				if (cmd.hasOption("password")) {
					keystorePass = cmd.getOptionValue("password");
				}
				else {
					System.err.println("[ERROR] Please provide a password for the keystore!");
					argumentParseErrors = true;
				}
			}
			
			if (cmd.hasOption("port")) {
				port = Integer.parseInt(cmd.getOptionValue("port"));
				
				// TODO: implement support for defining used port
				System.out.println("Sorry, port option has not been implemented yet :P");
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
					
				case SELFSIGNED:
					System.out.println("Certificate type: Self-signed");
					break;
					
				default:
					System.out.println("Certificate type: UNKNOWN");
					break;
				}
				
				// And finally, we'll check if we can connect the given URL
				// by using a keystore
				if (keystoreFile != null) {
					canConnectWithKeystore = checker.connectUsingKeystore(url, keystoreFile, keystorePass);
					System.out.println("Can connect using given keystore: " + 
							String.valueOf(canConnectWithKeystore).toUpperCase());
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
		formatter.printHelp("java -jar HttpsPingerApp.jar [options]", options);
	}

	private static void createOptions(Options options) {
		options.addOption("keystore", true, "Full path to keystore file");
		options.addOption("password", true, "Keystore password");
		options.addOption("url", true, "URL under test");
		options.addOption("port", true, "(not implemented) Port number of the service (default: 443)");
		options.addOption("help", false, "Displays this help documentation");
		options.addOption("debug", false, "Display javax.net.debug SSL debug messages");
	}

}
