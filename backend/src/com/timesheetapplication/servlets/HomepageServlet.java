package com.timesheetapplication.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.sun.crypto.provider.DESCipher;
import com.timesheetapplication.enums.Job;
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

@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpSession session;

	private ProjectService projectService = new ProjectService();

	private ActivityService activityService = new ActivityService();

	private DailyTimesheetService dtimesheetService = new DailyTimesheetService();
	
	private MonthlyTimeSheetService mtimesheetService = new MonthlyTimeSheetService();
	
	public HomepageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		session = request.getSession();

		JSONObject responseMessage = new JSONObject();
		
		System.out.println("homepage servlet knows about user = "
				+ session.getAttribute("loggedInUser"));
		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			System.out.println("you should login");
			try {
				responseMessage.put("ok", false);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		String phase = new String(request.getParameter("phase").toString());
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		switch (phase) {
			case "init":
				try {
					responseMessage.put("ok", true);
					responseMessage.put("name", loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
					responseMessage.put("job", loggedInUser.getJob());
					responseMessage.put("date", dateFormat.format(date));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
				break;
			case "loadProjectsForCurrentUser":
				List<Project> projects = projectService.getProjectsForEmployee(loggedInUser);
				
				ArrayList<String> projectNames = new ArrayList<String>();
				for (Project p : projects) {
					projectNames.add(p.getName());
				}
				try {
					responseMessage.put("ok", true);
					JSONArray array = new JSONArray(projectNames);
					responseMessage.put ("projects", array);
					System.out.println("sent back:" + array.toString());
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
				break;
			case "loadAllProjects":
				processLoadAllProjects(responseMessage, response);
				break;
			case "loadAllJobs" : 
				processLoadAllJobs(responseMessage, response);
				break;
			case "loadTodaysTimesheet":
				processLoadTodaysTimesheet(request, response, responseMessage);
				break;
			case "done":
				session.invalidate();
				break;
			case "loadAllMTimesheets":
				processLoadAllMonthlyTimesheets(request, response, responseMessage);
				break;
			case "changepassword":
				break;
			case "saveActivity" :
				processSaveActivity(request, response, responseMessage);
				break;
			default:
				break;
		}
	}
	
	private void processLoadAllMonthlyTimesheets(HttpServletRequest request, HttpServletResponse response, JSONObject responseMessage) {
		Employee e = (Employee) session.getAttribute("loggedInUser");
		
		ArrayList<String> nameList = new ArrayList<String>();
		ArrayList<String> statusList = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		for (MonthlyTimesheet m : e.getmTimesheets()) {
			c.setTime(m.getDate());
			
			
			// to be continued!
		}
		
		
		
	}

	private void processLoadTodaysTimesheet(HttpServletRequest request, HttpServletResponse response, JSONObject responseMessage) {
		Employee e = (Employee) session.getAttribute("loggedInUser");
		Date as = new Date();
		DailyTimeSheet ts = dtimesheetService.findDTSbyDateAndUser(as, e);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		if (ts == null || ts.getActivities().size() == 0) {
			ts = new DailyTimeSheet();
			ts.setOwner((Employee) session.getAttribute("loggedInUser"));
			ts.setDate(new Date());
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				Integer i = 0;
				ArrayList<String> durationArray = new ArrayList<String>();
				ArrayList<String> descriptionArray = new ArrayList<String>();
				ArrayList<String> projectArray = new ArrayList<String>();
				JSONArray array_du, array_de, array_p;
				for (Activity a : ts.getActivities()) {
					durationArray.add(a.getDuration().toString());
					descriptionArray.add(a.getDescription());
					projectArray.add(a.getProject().getName());
				}

				array_du = new JSONArray(durationArray);
				array_de = new JSONArray(descriptionArray);
				array_p = new JSONArray(projectArray);
				
				responseMessage.put("date", sdf.format(new Date()));
				responseMessage.put("size", "" + ts.getActivities().size());
				responseMessage.put("description", array_de);
				responseMessage.put("duration", array_du);
				responseMessage.put("project", array_p);
				responseMessage.put("ok", true);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		dtimesheetService.saveOrUpdateEvent(ts);
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processLoadAllJobs(JSONObject responseMessage,
			HttpServletResponse response) {
		ArrayList<String> jobNames = new ArrayList<String>();
		
		for (Job j : Job.values()) {
			jobNames.add(j.name());
		}
		
		try {
			responseMessage.put("ok", true);
			JSONArray array = new JSONArray(jobNames);
			responseMessage.put ("elements", array);
			System.out.println("sent back:" + array.toString());
			
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void processLoadAllProjects(JSONObject responseMessage, HttpServletResponse response) {
		List<Project> projects = projectService.loadAllProjects();
		
		ArrayList<String> projectNames = new ArrayList<String>();
		for (Project p : projects) {
			projectNames.add(p.getName());
		}
		try {
			responseMessage.put("ok", true);
			JSONArray array = new JSONArray(projectNames);
			responseMessage.put ("elements", array);
			System.out.println("sent back:" + array.toString());
			
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveActivity(HttpServletRequest request, HttpServletResponse response, JSONObject responseMessage) {

		String duration = request.getParameter("duration");
		String description = request.getParameter("description");
		String date = request.getParameter("date");
		String isExtra = request.getParameter("isExtra");
		String selectedProject = request.getParameter("project");

		System.out.println(duration + " " + description + " " + date + " " + isExtra + " " + selectedProject);

		Activity a = new Activity();
		a.setDescription(description);
		a.setDuration(Float.parseFloat(duration));
		a.setIsExtra(Boolean.parseBoolean(isExtra));
		a.setProject(projectService.findProjectByName(selectedProject));

		activityService.saveOrUpdate(a);

		Employee currentUser = (Employee) session.getAttribute("loggedInUser");
		Date currentDate = TSMUtil.convertStringToDate(date);

		DailyTimeSheet dts = dtimesheetService.findDTSbyDateAndUser(currentDate, currentUser);

		// if there is no dts for that particular date and employee create a
		// new one.
		if (dts == null) {
			dts = new DailyTimeSheet();
			dts.getActivities().add(a);
			dts.setOwner(currentUser);
			dts.setDate(currentDate);
		} else {
			dts.getActivities().add(a);
		}
		dtimesheetService.saveOrUpdateEvent(dts);

		Date dtrunc = TSMUtil.truncateDateToMonthsFirst(currentDate);
		MonthlyTimesheet mts = mtimesheetService.findMTSByDateAndUser(dtrunc, currentUser);

		if (mts == null) {
			mts = new MonthlyTimesheet();
			mts.setDate(dtrunc);
			mts.setOwner(currentUser);
			mts.getTimesheets().add(dts);
		}
		else {
			mts.getTimesheets().add(dts);
		}
		
		mtimesheetService.saveOrUpdate(mts);
		
		
		dts.setmTimesheet(mts);
		dtimesheetService.saveOrUpdateEvent(dts);
		
		// set the ts for this activity
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

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
