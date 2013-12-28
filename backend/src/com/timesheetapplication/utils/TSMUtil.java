package com.timesheetapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TSMUtil {

	public static String formatDate(Date d) {
		if (d == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		return sdf.format(d);
	}
	
	public static Date convertStringToDate(String d) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return formatter.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
