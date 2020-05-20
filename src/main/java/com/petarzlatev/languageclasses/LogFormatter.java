package com.petarzlatev.languageclasses;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.joda.time.DateTime;

public class LogFormatter extends Formatter {

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