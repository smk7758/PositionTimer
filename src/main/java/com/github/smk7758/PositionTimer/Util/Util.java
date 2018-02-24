package com.github.smk7758.PositionTimer.Util;

import java.time.Duration;
import java.time.temporal.Temporal;

public class Util {
	private Util() {
	}

	public static String getTimeText(Temporal start, Temporal end) {
		return getTimeText(getTime(start, end));
	}

	public static String getTimeText(Duration duration) {
		StringBuilder sb = new StringBuilder();
		int remove_length = 0;
		if (String.valueOf(duration.getSeconds()).length() <= 3) {
			remove_length = 6;
		} else {
			remove_length = 7;
		}
		sb.append(duration.getSeconds());
		sb.append('.');
		sb.append(duration.getNano()).delete(sb.length() - remove_length, sb.length());
		sb.append('s');
		return sb.toString();
	}

	public static Duration getTime(Temporal start, Temporal end) {
		return Duration.between(start, end);
	}

	public static String getPath(String... paths) {
		StringBuilder sb = new StringBuilder();
		sb.append(paths[0]);
		for (int i = 1; i < paths.length; i++) {
			sb.append('.');
			sb.append(paths[i]);
		}
		return sb.toString();
	}
}
