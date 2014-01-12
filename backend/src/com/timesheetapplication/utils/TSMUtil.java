package com.timesheetapplication.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

public class TSMUtil {

	public static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	public static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
	public static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	public static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	
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
	
	public static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
}
