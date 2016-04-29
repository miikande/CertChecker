package net.pinger.http;

import java.io.File;

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
		boolean isSelfSigned = false;
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
				System.out.println("SORRRYYY, not implemented yet :P");
			}
			
			if (cmd.hasOption("debug"))
				debug = cmd.getOptionValue("debug").equalsIgnoreCase("true") ? true : false;
			
		} catch (ParseException e) {
			System.err.println("[ERROR] Couldn't parse given arguments: " + e.getMessage());
			argumentParseErrors = true;
			//return;
		}
		
		if (!argumentParseErrors) {
			SSLConnection conn = new SSLConnection(debug);
			
			// First we'll test connection accepting self signed certs and then
			// without accepting self signed certs to see which one the server has...
			try {
				canConnect = conn.canConnectToAcceptSelfSigned(url);
			} catch (Exception e) { /* NO-OP */ }
			
			if (canConnect) {
				try {
					conn.canConnectToDoNotAcceptSelfSigned(url);
				}
				catch (Exception e) { 
					isSelfSigned = true;
				}
			}
			
			if (keystoreFile != null) {
				try {
					canConnectWithKeystore = conn.connectWithCert(url, keystoreFile, keystorePass);
				}
				catch (Exception e) { 
					// TODO: check why!
					canConnectWithKeystore = false;
				}
			}
		}
			
			
		if (!canConnect) {
			System.err.println("[ERROR] Couldn't connect to given URL: " + url);
			System.exit(2);
		}
		else {
			System.out.println("Connection was successfully established!");
			System.out.println("URL: " + url);
			
			String certType = isSelfSigned ? "self-signed" : "CA signed";
			System.out.println("Certificate type: " + certType);
			
			if (keystoreFile != null) {
				System.out.println("Can connect using keystore: " + canConnectWithKeystore);
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
	}

}
