package com.tom.apis;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.tom.lib.Configs;

public class TMLogger {
	public static org.apache.logging.log4j.Logger log = LogManager.getLogger(Configs.Modid);//FMLLog

	public static void catching(Throwable t) {
		log.catching(t);
	}

	public static void error(Object message) {
		log.error(message);
	}

	public static void error(String message) {
		log.error(message);
	}

	public static void info(Object message) {
		log.info(message);
	}

	public static void info(String message) {
		log.info(message);
	}

	public static void warn(Object message) {
		log.warn(message);
	}

	public static void warn(String message) {
		log.warn(message);
	}

	public static void bigWarn(String format, Object... data) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		log.log(Level.WARN, "****************************************");
		log.log(Level.WARN, "* "+String.format(format, data));
		for (int i = 2; i < 8 && i < trace.length; i++)
		{
			log.log(Level.WARN, String.format("*  at %s%s", trace[i].toString(), i == 7 ? (trace.length > 5 ? " " + (trace.length - 5) + " more..." : "") : ""));
		}
		log.log(Level.WARN, "****************************************");
	}
	public static void bigError(String format, Object... data) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		log.log(Level.ERROR, "****************************************");
		log.log(Level.ERROR, "* "+String.format(format, data));
		for (int i = 2; i < 8 && i < trace.length; i++)
		{
			log.log(Level.ERROR, String.format("*  at %s%s", trace[i].toString(), i == 7 ? (trace.length > 5 ? " " + (trace.length - 5) + " more..." : "") : ""));
		}
		log.log(Level.ERROR, "****************************************");
	}
	public static void bigCatching(Throwable t, String format, Object... data) {
		StackTraceElement[] trace = t.getStackTrace();
		log.log(Level.ERROR, "****************************************");
		log.log(Level.ERROR, "* Caught: "+t.getClass() + ": "+t.getMessage());
		log.log(Level.ERROR, "* "+String.format(format, data));
		for (int i = 0; i < 5 && i < trace.length; i++)
		{
			log.log(Level.ERROR, String.format("*  at %s%s", trace[i].toString(), i == 4 ? (trace.length > 5 ? " " + (trace.length - 5) + " more..." : "") : ""));
		}
		log.log(Level.ERROR, "****************************************");
	}
	public static void catching(Throwable t, String extra) {
		error(extra);
		catching(t);
	}
}
