package com.timesheetapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TSMUtil {

	public static String formatDate(Date d) {
		if (d == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("DD/MM/YYYY");
		return sdf.format(d);
	}

}
