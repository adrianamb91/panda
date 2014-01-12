package com.timesheetapplication.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.timesheetapplication.model.Activity;
import com.timesheetapplication.model.DailyTimeSheet;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.ActivityService;
import com.timesheetapplication.service.DailyTimesheetService;
import com.timesheetapplication.service.MonthlyTimeSheetService;
import com.timesheetapplication.service.ProjectService;
import com.timesheetapplication.utils.TSMUtil;

/**
 * Servlet implementation class DivManagerServlet
 */
@WebServlet("/DivManagerServlet")
public class DivManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	HttpSession session;

	private ActivityService activityService = new ActivityService();

	private DailyTimesheetService dtimesheetService = new DailyTimesheetService();

	private MonthlyTimeSheetService mtimesheetService = new MonthlyTimeSheetService();

	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DivManagerServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();

		JSONObject responseMessage = new JSONObject();
		
		String phase = new String(request.getParameter("phase").toString());
		switch (phase) {
		case "loadDTS":
			loadDTS(request, responseMessage, response);
			break;
		case "saveActivity":
			saveActivity(request, responseMessage, response);
			break;
		case "loadEmployees":
			loadEmployeesFromDivision(request, responseMessage, response);
			break;
		default:
			break;
		}
		
	}

	private void loadEmployeesFromDivision(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Employee loggedInUser = (Employee)session.getAttribute("loggedInUser");
		
	}

	private void saveActivity(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String duration = request.getParameter("duration");
		String description = request.getParameter("description");
		String date = request.getParameter("date");
		String isExtra = request.getParameter("isExtra");

		Float oldDuration = null;
		Activity a = null;
		if (TSMUtil.isValidString(request.getParameter("old_duration"))
				&& TSMUtil.isValidString(request
						.getParameter("old_description"))
				&& TSMUtil.isValidString(request
						.getParameter("old_projectName"))
				&& TSMUtil.isValidString(request.getParameter("old_date"))) {

			oldDuration = Float
					.parseFloat(request.getParameter("old_duration"));
			String oldDescription = request.getParameter("old_description");
			Date oldDate = TSMUtil.convertStringToDate(request
					.getParameter("old_date"));
			System.out.println("old: " + oldDuration + " " + oldDescription
					+ " " + oldDate.getTime() + " ");
			a = activityService.findActivityByDateDurationDesc(
					oldDate, oldDuration, oldDescription);
		}

		System.out.println("new: " + duration + " " + description + " " + date
				+ " " + isExtra);

		Employee currentUser = (Employee) session.getAttribute("loggedInUser");
		Date currentDate = TSMUtil.convertStringToDate(date);
		Date dtrunc = TSMUtil.truncateDateToMonthsFirst(currentDate);		
		MonthlyTimesheet mts = mtimesheetService.findMTSByDateAndUser(dtrunc,
				currentUser);

		if (mts == null) {
			mts = new MonthlyTimesheet();
			mts.setDate(dtrunc);
			mts.setOwner(currentUser);
		} 
		mtimesheetService.saveOrUpdate(mts);
		
		DailyTimeSheet dts = dtimesheetService.findDTSbyDateAndUser(
				currentDate, currentUser);

		// if there is no dts for that particular date and employee create a
		// new one.
		if (dts == null) {
			dts = new DailyTimeSheet();
			dts.setOwner(currentUser);
			dts.setDate(currentDate);
			dts.setmTimesheet(mts);
		}
		dtimesheetService.saveOrUpdateEvent(dts);
		
		a = new Activity();
		a.setDescription(description);
		a.setDuration(Float.parseFloat(duration));
		a.setIsExtra(Boolean.parseBoolean(isExtra));
		a.setTimesheet(dts);
		
		activityService.saveOrUpdate(a);

		try {
			responseMessage.put("ok", true);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void loadDTS(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Employee e = (Employee) session.getAttribute("loggedInUser");

		String dateString = request.getParameter("date");
		Date as;
		if (dateString != null) {
			SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yy");
			try {
				as = dt.parse(dateString);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				as = new Date();
			}
		} else {
			as = new Date();
		}
		DailyTimeSheet dts = dtimesheetService.findDTSbyDateAndUser(as, e);

		if (dts == null) {
			try {
				responseMessage.put("ok", false);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
				return;
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		List<Activity> acts = activityService.findActivitiesByDTS(dts);
		if (acts == null || acts.size() == 0) {
			try {
				responseMessage.put("ok", false);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
				return;
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		

		try {
			ArrayList<String> durationArray = new ArrayList<String>();
			ArrayList<String> descriptionArray = new ArrayList<String>();
			JSONArray array_du, array_de;

			for (Activity a : acts) {
				durationArray.add(a.getDuration().toString());
				descriptionArray.add(a.getDescription());
			}

			array_du = new JSONArray(durationArray);
			array_de = new JSONArray(descriptionArray);

			responseMessage.put("date", TSMUtil.formatDate(as));
			responseMessage.put("size", "" + acts.size());
			responseMessage.put("description", array_de);
			responseMessage.put("duration", array_du);
			responseMessage.put("ok", true);

		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
