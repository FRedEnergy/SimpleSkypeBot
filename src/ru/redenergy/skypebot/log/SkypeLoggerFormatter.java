package ru.redenergy.skypebot.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SkypeLoggerFormatter extends Formatter{

	@Override
	public String format(LogRecord record) {
		return String.format("[%s] %s \n", record.getLevel().getName(), record.getMessage());
	}

}
