package com.timesheetapplication.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.MonthlyTimesheet;
import com.timesheetapplication.service.ActivityService;
import com.timesheetapplication.service.DailyTimesheetService;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.MonthlyTimeSheetService;
import com.timesheetapplication.utils.TSMUtil;

/**
 * Servlet implementation class DepartmentManagerServlet
 */
@WebServlet("/DepartmentManagerServlet")
public class DepartmentManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpSession session;
	DepartmentService departmentService = new DepartmentService();

	EmployeeService employeeService = new EmployeeService();

	MonthlyTimeSheetService mTimesheetService = new MonthlyTimeSheetService();

	DailyTimesheetService dTimesheetService = new DailyTimesheetService();

	ActivityService activityService = new ActivityService();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DepartmentManagerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();

		session = request.getSession();
		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		switch (phase) {
		case "loadEmployees":
			processLoadManagersEmployees(loggedInUser, responseMessage, response);
			break;
		case "reviewLastMTS":
			processReviewLastMTS(request, responseMessage, response);
			break;
		case "reviewMTSforUserInInterval":
			processReviewMTSforUserInInterval(request, responseMessage, response);
		default:
			break;
		}

	}

	private void processReviewMTSforUserInInterval(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String ename = request.getParameter("name");
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		
		System.out.println("from: " + from + "\n" + "to: " + to);
		
		if (TSMUtil.isNotEmptyOrNull(ename)) {
			Employee e = employeeService.findEmployeeByFirstAndLastName(ename);
			if (e != null) {
				List<DailyTimeSheet> dtsheets = dTimesheetService.loadDTSinInterval(e, TSMUtil.convertStringToDate(from),
						TSMUtil.convertStringToDate(to));
				if (dtsheets != null) {

					List<Activity> crtActivities = new ArrayList<Activity>();
					List<Activity> allActivities = new ArrayList<Activity>();
					Map<String, Float> chartData = new HashMap<String, Float>();

					for (DailyTimeSheet dts : dtsheets) {
						crtActivities = activityService.findActivitiesByDTS(dts);
						if (crtActivities != null && crtActivities.size() > 0) {
							allActivities.addAll(crtActivities);
						}
					}

					ArrayList<String> dateArray = new ArrayList<String>();
					ArrayList<String> durationArray = new ArrayList<String>();
					ArrayList<String> descriptionArray = new ArrayList<String>();
					ArrayList<String> projectArray = new ArrayList<String>();

					JSONArray array_du, array_de, array_p, array_da;

					for (Activity a : allActivities) {
						dateArray.add(TSMUtil.formatDate(a.getTimesheet().getDate()));
						durationArray.add(a.getDuration().toString());
						descriptionArray.add(a.getDescription());
						projectArray.add(a.getProject().getName());
						
						if (!chartData.containsKey(a.getProject().getName())) {
							chartData.put(a.getProject().getName(), a.getDuration());
						}
						else {
							Float oldValue = chartData.get(a.getProject().getName());
							chartData.put(a.getProject().getName(), oldValue + a.getDuration());
						}
					}	

					array_da = new JSONArray(dateArray);
					array_du = new JSONArray(durationArray);
					array_de = new JSONArray(descriptionArray);
					array_p = new JSONArray(projectArray);
					
					System.out.println(array_da);
					System.out.println(chartData);
					
					JSONObject chartFormat = new JSONObject(chartData);
					System.out.println(chartFormat.toString());
					System.out.println(chartFormat);
					
					JSONArray chartJSON = null;
					
					StringBuilder sb = new StringBuilder();
					sb.append("[[");
					
					int iter = 0;
					for (String p : chartData.keySet()) {
						sb.append("['");
						sb.append(p);
						sb.append("', ");
						sb.append(chartData.get(p));
						sb.append("]");
						if (iter < chartData.keySet().size() - 1) {
							sb.append(", ");
						}
						iter ++ ;
					}
					
					sb.append("]]");
					
					try {
						responseMessage.put("date", array_da);
						responseMessage.put("description", array_de);
						responseMessage.put("duration", array_du);
						responseMessage.put("project", array_p);
						responseMessage.put("size", allActivities.size());
						responseMessage.put("ok", true);
						chartJSON = new JSONArray(sb.toString());
						responseMessage.put("chartDataJSON", chartJSON);
						responseMessage.put("chartData", sb.toString());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				try {
					responseMessage.put("ok", false);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processReviewLastMTS(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		String ename = request.getParameter("name");

		Employee e = null;
		List<DailyTimeSheet> dts = null;
		List<Activity> allActivities = new ArrayList<Activity>();
		List<Activity> crtActivities = new ArrayList<Activity>();

		if (TSMUtil.isNotEmptyOrNull(ename)) {
			e = employeeService.findEmployeeByFirstAndLastName(ename);

			if (e != null) {

				// aici se afiseaza numai pentru luna curenta pentru ca din alta
				// luna e prea greu... fac maine cu iubi:X:X
				Date date = TSMUtil.truncateDateToMonthsFirst(new Date());
				MonthlyTimesheet mts = mTimesheetService.findMTSByDateAndUser(date, e);

				if (mts != null && mts.getId() != null) {
					dts = dTimesheetService.findAllDTFromMTS(mts);
					if (dts != null) {
						for (DailyTimeSheet crtDTS : dts) {
							crtActivities = activityService.findActivitiesByDTS(crtDTS);
							if (crtActivities != null && crtActivities.size() > 0) {
								allActivities.addAll(crtActivities);
							}
						}

						ArrayList<String> dateArray = new ArrayList<String>();
						ArrayList<String> durationArray = new ArrayList<String>();
						ArrayList<String> descriptionArray = new ArrayList<String>();
						ArrayList<String> projectArray = new ArrayList<String>();

						JSONArray array_du, array_de, array_p, array_da;

						for (Activity a : allActivities) {
							dateArray.add(TSMUtil.formatDate(a.getTimesheet().getDate()));
							durationArray.add(a.getDuration().toString());
							descriptionArray.add(a.getDescription());
							projectArray.add(a.getProject().getName());
						}

						array_da = new JSONArray(dateArray);
						array_du = new JSONArray(durationArray);
						array_de = new JSONArray(descriptionArray);
						array_p = new JSONArray(projectArray);

						try {
							responseMessage.put("date", array_da);
							responseMessage.put("description", array_de);
							responseMessage.put("duration", array_du);
							responseMessage.put("project", array_p);
							responseMessage.put("size", allActivities.size());
							responseMessage.put("ok", true);
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void processLoadManagersEmployees(Employee loggedInUser, JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Department d = departmentService.findDepartmentByManager(loggedInUser);
		List<Employee> employees = null;
		ArrayList<String> employeesFLN = null;
		if (d != null) {
			employees = employeeService.findAllEmployeesByDepartment(d);
			employeesFLN = new ArrayList<String>();
			for (Employee e : employees) {
				employeesFLN.add(e.getFirstName() + " " + e.getLastName());
			}
		}
		try {
			if (employeesFLN != null) {
				responseMessage.put("ok", true);
				JSONArray emps = new JSONArray(employeesFLN);
				// System.out.println(emps.toString());
				responseMessage.put("elements", emps);
				responseMessage.put("size", employeesFLN.size());
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
