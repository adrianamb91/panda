package com.timesheetapplication.servlets;

import java.io.IOException;
import java.text.DateFormat;
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
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.ActivityService;
import com.timesheetapplication.service.DailyTimesheetService;
import com.timesheetapplication.service.ProjectService;
import com.timesheetapplication.utils.TSMUtil;

@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpSession session;

	private ProjectService projectService = new ProjectService();

	private ActivityService activityService = new ActivityService();

	private DailyTimesheetService dtimesheetService = new DailyTimesheetService();
	
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
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
			case "done":
				session.invalidate();
				break;
			case "changepassword":
				break;
				
			case "saveActivity" :
				processSaveActivity(request);
				break;
			default:
				break;
		}
	}
	
	private void processSaveActivity(HttpServletRequest request) {

		String duration = request.getParameter("duration");
		String description = request.getParameter("description");
		String date = request.getParameter("date");
		String isExtra = request.getParameter("isExtra");
		String selectedProject = request.getParameter("project");

		System.out.println(duration + " " + description + " " + date + " "
				+ isExtra + " " + selectedProject);

		Activity a = new Activity();
		a.setDescription(description);
		a.setDuration(Float.parseFloat(duration));
		a.setIsExtra(Boolean.parseBoolean(isExtra));
		a.setProject(projectService.findProjectByName(selectedProject));

		activityService.saveOrUpdate(a);

		Employee e = (Employee) session.getAttribute("loggedInUser");
		Date as = TSMUtil.convertStringToDate(date);

		DailyTimeSheet ts = dtimesheetService.findDTSbyDateAndUser(as, e);

		// if there is no dts for that particular date and employee create a new
		// one.
		if (ts == null) {
			ts = new DailyTimeSheet();
			ts.getActivities().add(a);
			ts.setOwner((Employee) session.getAttribute("loggedInUser"));
			ts.setDate(TSMUtil.convertStringToDate(date));
		} else {
			ts.getActivities().add(a);
		}
		dtimesheetService.saveOrUpdateEvent(ts);
		
		// set the ts for this activity
		a.setTimesheet(ts);
		activityService.saveOrUpdate(a);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
