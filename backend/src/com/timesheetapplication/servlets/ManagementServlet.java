package com.timesheetapplication.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.timesheetapplication.enums.Job;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.DivisionService;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.ProjectService;
import com.timesheetapplication.utils.TSMUtil;

@WebServlet("/ManagementServlet")
public class ManagementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private EmployeeService employeeService = new EmployeeService();

	private DivisionService divisionService = new DivisionService();

	private DepartmentService departmentService = new DepartmentService();

	private ProjectService projectService = new ProjectService();

	public ManagementServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();

		switch (phase) {
		case "loadAllPossibleManagers":
			processLoadAllPossibleManagers(responseMessage, response);
			break;
		case "loadAllEmployees":
			processLoadAllEmployees(responseMessage, response);
			break;
		case "loadAllDivisions":
			processLoadAllDivisions(responseMessage, response);
			break;
		case "loadAllDepartments":
			processLoadAllDepartments(responseMessage, response);
			break;
		case "saveNewEmployee":
			processSaveEmployee(request);
			break;
		case "saveDepartment":
			processSaveDepartment(request);
			break;
		case "saveDivision":
			processSaveDivision(request, responseMessage, response);
			break;
		case "removeDivision":
			processRemoveDivision(request, responseMessage, response);
			break;
		case "removeDepartment":
			processRemoveDepartment(request, responseMessage, response);
			break;
		case "removeEmployee":
			processRemoveEmployee(request, responseMessage, response);
			break;
		case "editDivision":
			processEditDivision(request, responseMessage, response);
			break;
		case "editDepartment":
			processEditDepartment(request, responseMessage, response);
		default:
			break;
		}
	}

	private void processEditDepartment(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		String oldname = request.getParameter("oldname");
		String newName = request.getParameter("newname");

		if (!TSMUtil.isNotEmptyOrNull(oldname)) {
			TSMUtil.sendFalseInPage(responseMessage, response);
			return;
		}

		Department d = departmentService.findDepartmentByName(oldname);
		if (d == null) {
			TSMUtil.sendFalseInPage(responseMessage, response);
			return;
		}

		d.setName(newName);
		departmentService.saveOrUpdate(d);
		
		try {
			responseMessage.put("ok", true);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processEditDivision(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		
		String oldName = request.getParameter("oldname");
		String newName = request.getParameter("newname");
		
		if (TSMUtil.isNotEmptyOrNull(oldName) && TSMUtil.isNotEmptyOrNull(newName)) {
			try {
				if (divisionService.replaceDivisionName(oldName, newName)) {
					responseMessage.put("ok", true);
				} else {
					responseMessage.put("ok", false);
				}

				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void processRemoveEmployee(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String username = request.getParameter("name");

		if (TSMUtil.isNotEmptyOrNull(username)) {
			employeeService.removeEmployeeByUsername(username);
			try {
				responseMessage.put("ok", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processRemoveDepartment(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		String departmentName = request.getParameter("name");
		if (TSMUtil.isNotEmptyOrNull(departmentName)) {
			departmentService.remove(departmentName);
			try {
				responseMessage.put("ok", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processRemoveDivision(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String divisionName = request.getParameter("name");
		if (TSMUtil.isNotEmptyOrNull(divisionName)) {
			divisionService.remove(divisionName);
			try {
				responseMessage.put("ok", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				responseMessage.put("ok", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processLoadAllEmployees(JSONObject responseMessage, HttpServletResponse response) {
		List<Employee> employees = employeeService.loadAllEmployees();

		if (employees.size() == 0) {
			try {
				responseMessage.put("ok", false);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		List<String> usernames = new ArrayList<String>();
		List<String> firstNames = new ArrayList<String>();
		List<String> lastNames = new ArrayList<String>();
		List<String> emails = new ArrayList<String>();
		List<String> jobs = new ArrayList<String>();
		List<String> departments = new ArrayList<String>();

		for (Employee e : employees) {
			usernames.add(e.getUsername());
			firstNames.add(e.getFirstName());
			lastNames.add(e.getLastName());
			emails.add(e.getEmail());
			jobs.add(e.getJob().toString());
			if (e.getDepartment() != null) {
				departments.add(e.getDepartment().getName());
			} else {
				departments.add("-");
			}
		}

		JSONArray _usernames = new JSONArray(usernames);
		JSONArray _firstnames = new JSONArray(firstNames);
		JSONArray _lastnames = new JSONArray(lastNames);
		JSONArray _emails = new JSONArray(emails);
		JSONArray _jobs = new JSONArray(jobs);
		JSONArray _departments = new JSONArray(departments);

		try {
			responseMessage.put("usernames", _usernames);
			responseMessage.put("firstnames", _firstnames);
			responseMessage.put("lastnames", _lastnames);
			responseMessage.put("emails", _emails);
			responseMessage.put("jobs", _jobs);
			responseMessage.put("departments", _departments);
			responseMessage.put("size", employees.size());
			responseMessage.put("ok", true);

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}

	}

	private void processLoadAllPossibleManagers(JSONObject responseMessage, HttpServletResponse response) {
		List<Employee> employees = employeeService.loadAllEmployees();

		if (employees.size() == 0) {
			try {
				responseMessage.put("ok", false);
				response.setContentType("application/json; charset=UTF-8");
				response.getWriter().write(responseMessage.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		List<String> names = new ArrayList<String>();

		for (Employee e : employees) {
			names.add(e.getFirstName() + " " + e.getLastName());
		}

		JSONArray _usernames = new JSONArray(names);

		try {
			responseMessage.put("elements", _usernames);
			responseMessage.put("size", employees.size());
			responseMessage.put("ok", true);

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}

	}

	private void processSaveEmployee(HttpServletRequest request) {

		String username = request.getParameter("name");
		String pass = request.getParameter("pass");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String project = request.getParameter("project");
		String job = request.getParameter("job");
		String depart = request.getParameter("depart");

		System.out.println(username + " " + pass + " " + firstName + " " + lastName + " " + email + " " + project + " " + job + " " + depart);

		Employee e = new Employee();
		e.setUsername(username);
		e.setPassword(pass);
		e.setFirstName(firstName);
		e.setLastName(lastName);
		e.setEmail(email);
		e.setJob(Job.valueOf(job));
		e.getProjects().add(projectService.findProjectByName(project));
		e.setDepartment(departmentService.findDepartmentByName(depart));

		employeeService.saveOrUpdate(e);
	}

	private void processLoadAllDepartments(JSONObject responseMessage, HttpServletResponse response) {

		List<Department> departments = departmentService.loadAll();

		ArrayList<String> departmentNames = new ArrayList<String>();
		ArrayList<String> divisionNames = new ArrayList<String>();
		ArrayList<String> managerNames = new ArrayList<String>();
		for (Department d : departments) {
			departmentNames.add(d.getName());

			if (d.getDivision() != null) {
				divisionNames.add(d.getDivision().getName());
			} else {
				divisionNames.add("-");
			}

			if (d.getManager() != null) {
				managerNames.add(d.getManager().getFirstName() + " " + d.getManager().getLastName());
			} else {
				managerNames.add("-");
			}
		}
		try {
			responseMessage.put("ok", true);
			JSONArray _departmentNames = new JSONArray(departmentNames);
			JSONArray _divisionNames = new JSONArray(divisionNames);
			JSONArray _managerNames = new JSONArray(managerNames);
			responseMessage.put("elements", _departmentNames);
			responseMessage.put("divisions", _divisionNames);
			responseMessage.put("managers", _managerNames);
			responseMessage.put("size", departments.size());
			System.out.println("sent back:" + _departmentNames.toString());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveDepartment(HttpServletRequest request) {

		String name = request.getParameter("name");
		String managerName = request.getParameter("manager");
		String divisionName = request.getParameter("division");

		System.out.println("--+++--" + managerName);
		
		Department d = new Department();

		Division div = divisionService.findDivisionByName(divisionName);
		Employee manager = null;
		if (TSMUtil.isNotEmptyOrNull(managerName)) {
			manager = employeeService.findEmployeeByFirstAndLastName(managerName);
		}

		d.setName(name);
		d.setDivision(div);
		if (manager != null) {
			d.setManager(manager);
		}

		departmentService.saveOrUpdate(d);
		
		if (manager != null) {
			manager.setDepartment(d);
			employeeService.saveOrUpdate(manager);
		}
	}

	private void processLoadAllDivisions(JSONObject responseMessage, HttpServletResponse response) {
		List<Division> divisions = divisionService.loadAll();

		ArrayList<String> divisionNames = new ArrayList<String>();
		ArrayList<String> managersNames = new ArrayList<String>();

		for (Division d : divisions) {
			divisionNames.add(d.getName());

			if (d.getManager() != null) {
				managersNames.add(d.getManager().getFirstName() + " " + d.getManager().getLastName());
			} else {
				managersNames.add("-");
			}
		}
		try {
			responseMessage.put("ok", true);
			JSONArray names = new JSONArray(divisionNames);
			JSONArray managers = new JSONArray(managersNames);

			responseMessage.put("elements", names);
			responseMessage.put("managers", managers);
			responseMessage.put("size", divisions.size());

			System.out.println(names.toString());
			System.out.println(managers.toString());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveDivision(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {

		String divName = request.getParameter("name");
		String manName = request.getParameter("manager");

		System.out.println("request: " + divName + " " + manName);

		Division d = new Division();
		d.setName(divName);

		Employee manager = null;
		if (TSMUtil.isNotEmptyOrNull(manName)) {
			manager = employeeService.findEmployeeByFirstAndLastName(manName);
			d.setManager(manager);
		} else {
			System.out.println("-----manager null");
		}

		divisionService.saveOrUpdate(d);

		try {
			responseMessage.put("ok", true);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

}
