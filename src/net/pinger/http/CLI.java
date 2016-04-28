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
		boolean errors = false;
		File keystoreFile = null;
		String keystorePass = null;
		String url = null;
		int port = 443;
		boolean debug = false;
		
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
			
			// Check the args we got from user
			if (cmd.hasOption("url")) {
				url = cmd.getOptionValue("url");
			}
			else {
				System.err.println("[ERROR] URL is required parameter!");
				errors = true;
			}
			
			if (cmd.hasOption("keystore")) {
				String keystoreFileStr = cmd.getOptionValue("keystore");
				keystoreFile = new File(keystoreFileStr);
				
				if (!keystoreFile.canRead()) {
					System.err.println("[ERROR] Can't access given keystore file: " + keystoreFileStr);
					errors = true;
				}
				
				if (cmd.hasOption("password")) {
					keystorePass = cmd.getOptionValue("password");
				}
				else {
					System.err.println("[ERROR] Please provide a password for the keystore!");
					errors = true;
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
			errors = true;
			return;
		}
		
		if (!errors) {
			SSLConnection conn = new SSLConnection(debug);
			boolean success = true;
			
			try {
				if (keystoreFile == null) {
					success = conn.connectNoCert(url);	
				}
				else {
					success = conn.connectWithCert(url, keystoreFile, keystorePass);
				}
			} 
			catch (ValidatorException e) {
				System.err.println("[ERROR] Can't find valid certificate for given URL!");
				System.exit(2);
			}
			catch (Exception e) {
				System.err.println("[ERROR] Got into problems while connecting to given URL: " + e.getMessage());
				System.exit(2);
			}
			
			if (!success) {
				System.err.println("[ERROR] Couldn't connect to given URL...");
				System.exit(2);
			}
			else {
				System.out.println("Connection was successfully established!");
			}
			
		}
		else {
			System.out.println("Please solve above issues and try again!");
		}
		
	}

	private static void displayHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("HTTPS Pinging Application", options);
	}

	private static void createOptions(Options options) {
		options.addOption("keystore", true, "Full path to keystore file");
		options.addOption("password", true, "Keystore password");
		options.addOption("url", true, "URL under test");
		options.addOption("port", true, "Port number of the service (default: 443)");
		options.addOption("help", false, "Display help documentation");
	}

}
