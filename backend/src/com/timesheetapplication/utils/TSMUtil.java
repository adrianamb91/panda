package com.timesheetapplication.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class TSMUtil {

	public static String formatDate(Date d) {
		if (d == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(d);
	}
	
	public static Date convertStringToDate(String d) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return formatter.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date truncateDateToMonthsFirst(Date as) {
		Calendar c = Calendar.getInstance();
		c.setTime(as);
		c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
		
		Date d = c.getTime();
		return c.getTime();
	}

	public static Boolean isValidString(String str) {
		if (str != null && str.length() > 0 && !str.equals(" ") && !str.equalsIgnoreCase("undefined")) {
			return true;
		}
		return false;
	}
	
	public static Boolean isNotEmptyOrNull(String str) {
		if (str != null && !str.equals("")) {
			return true;
		}
		return false;
	}
	
	public static void sendFalseInPage(JSONObject responseMessage, HttpServletResponse response) {
		try {
			responseMessage.put("ok", false);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
