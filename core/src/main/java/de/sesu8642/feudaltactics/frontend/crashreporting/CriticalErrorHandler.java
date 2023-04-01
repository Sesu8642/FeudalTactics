// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.crashreporting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import de.sesu8642.feudaltactics.FeudalTactics;

/**
 * JUL handler that is supposed to be used as the target for a MemoryHandler
 * (see logging.properties). It buffers the records spit out by the
 * MemoryHandler until a critical error is reached. This is also the one that
 * caused the MemoryHandler to escalate in the first place. Then, all the
 * buffered logs are formatted and passed to the GameCrasher.
 */
public class CriticalErrorHandler extends Handler {

	private List<LogRecord> bufferedRecords = new ArrayList<>();

	public CriticalErrorHandler() {
		super();
		setFormatter(new SimpleFormatter());
	}

	@Override
	public void publish(LogRecord logRecord) {
		bufferedRecords.add(logRecord);
		// if a critical error happened, crash!
		if (logRecord.getLevel() == Level.SEVERE && logRecord.getThrown() != null) {
			StringBuilder builder = new StringBuilder();
			for (LogRecord bufferedRecord : bufferedRecords) {
				String formattedRecord = getFormatter().format(bufferedRecord);
				builder.append(formattedRecord);
			}
			FeudalTactics.game.getComponent().getGameCrasher().crashAfterGeneratingReport(builder.toString(),
					logRecord.getThrown());
		}
	}

	@Override
	public void flush() {
		// nothing to do
	}

	@Override
	public void close() throws SecurityException {
		// nothing to do
	}

}
