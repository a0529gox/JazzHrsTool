package com.mysterion.jht2.log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AnnoyLogger {
	
	private static Logger log;
	
	private static final Path DEFAULT_PATH = Paths.get(System.getProperty("user.dir"), "log.txt");

	private AnnoyLogger() {
		// TODO Auto-generated constructor stub
	}
	
	private static Logger get() {
		if (log == null) {
			try {
				log = Logger.getAnonymousLogger();
				
				FileHandler handler = new FileHandler(DEFAULT_PATH.toString());
				handler.setFormatter(new SimpleFormatter());
				log.addHandler(handler);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return log;
	}

	public static void severe(Throwable t) {
		get().log(Level.SEVERE, "catch a throwable:", t);
	}
	
	public static void info(String msg) {
		get().info(msg);
	}
	
	public static void warning(String msg) {
		get().warning(msg);
	}
	
	public static void severe(String msg) {
		get()	.severe(msg);
	}
}
