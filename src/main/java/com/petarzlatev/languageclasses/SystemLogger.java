package com.petarzlatev.languageclasses;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.joda.time.DateTime;

public class SystemLogger {

	private static String logFileName;

	/****************************************************
	 * Getter/Setter
	 ****************************************************/
	public static String getLogFileName() {
		return logFileName;
	}

	private static void setLogFileName(String fileName) {
		logFileName = fileName;
	}

	/****************************************************
	 * Methods
	 ****************************************************/
	public static void runLogger() {
		Logger logger = Logger.getLogger("LogFile");
		FileHandler fh;
		String logPath;

		try {
			logPath = SystemProperties.getLogPath();
			if (logPath != null) {
				setLogFileName(logPath + "ErrorLog_" + DateTimeFormatter.ofPattern("MM_dd_yyyy").format(LocalDate.now())
						+ ".log");
				fh = new FileHandler(getLogFileName(), true);
				fh.setFormatter(new LogFormatter());
				logger.addHandler(fh);
				logger.setLevel(Level.ALL);
				fh.close();
			} else {
				System.out.println(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "LOG_PATH");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void logPrivateEvent(String message, Level level, String sourceClassName, String sourceMethodName) {
		try {
			Logger logger = Logger.getLogger("LogFile");
			FileHandler fh = new FileHandler(getLogFileName(), true);
			fh.setFormatter(new LogFormatter());
			logger.addHandler(fh);
			LogRecord log = new LogRecord(level, message);
			log.setSourceClassName(sourceClassName);
			log.setSourceMethodName(sourceMethodName);
			logger.log(log);
			fh.close();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logEvent(String message, Level level, String sourceClassName) {
		logPrivateEvent(message, level, sourceClassName, "");
	}

	public static void logEvent(String message, Level level, String sourceClassName, String sourceMethodName) {
		logPrivateEvent(message, level, sourceClassName, sourceMethodName);
	}

}

class LogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		if (record.getSequenceNumber() == 0) {
			sb.append("|||||||||||||||||||||||||||TheBoss version 0.0.1|||||||||||||||||||||||||||").append("\n");
		}
		sb.append("Msg Num: ").append(record.getSequenceNumber()).append("**************************")
				.append(new DateTime(record.getMillis())).append("**************************").append("\n");
		sb.append(record.getSourceClassName()).append("\n");
		sb.append(record.getLevel()).append(':');
		sb.append(record.getMessage()).append('\n');

		if (!record.getSourceMethodName().isEmpty()) {
			sb.append("|||||||||||||||||||||||||||" + record.getSourceMethodName() + "|||||||||||||||||||||||||||")
					.append('\n');
		}

		return sb.toString();
	}
}
