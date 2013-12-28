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
import org.json.JSONObject;

import com.timesheetapplication.enums.Job;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.DivisionService;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.ProjectService;

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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();
		
		switch(phase) {
		case "loadAllDivisions" :
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
			processSaveDivision(request);
			break;
		default:
			break;
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

	private void processLoadAllDepartments(JSONObject responseMessage,
			HttpServletResponse response) {

		List<Department> departments = departmentService.loadAll();

		ArrayList<String> departmentNames = new ArrayList<String>();
		for (Department d : departments) {
			departmentNames.add(d.getName());
		}
		try {
			responseMessage.put("ok", true);
			JSONArray array = new JSONArray(departmentNames);
			responseMessage.put("elements", array);
			System.out.println("sent back:" + array.toString());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveDepartment(HttpServletRequest request) {

		String name = request.getParameter("name");
		String manangerName = request.getParameter("mananger");
		String divisionName = request.getParameter("division");
		
		Department d = new Department();
		
		Division div = divisionService.findDivisionByName(divisionName);
		Employee manager = employeeService.findEmployeeByUsername(manangerName);
		
		d.setName(name);
		d.setDivision(div);
		d.setManager(manager);

		departmentService.saveOrUpdate(d);
	}

	private void processLoadAllDivisions(JSONObject responseMessage, HttpServletResponse response) {
		List<Division> divisions = divisionService.loadAll();
		
		ArrayList<String> divisionNames = new ArrayList<String>();
		for (Division d : divisions) {
			divisionNames.add(d.getName());
		}
		try {
			responseMessage.put("ok", true);
			JSONArray array = new JSONArray(divisionNames);
			responseMessage.put ("elements", array);
			System.out.println("sent back:" + array.toString());
			
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveDivision(HttpServletRequest request) {

		String divName = request.getParameter("name");
		String manName = request.getParameter("manager");
		
		System.out.println("request: " + divName + " " + manName);
		
		Employee manager = employeeService.findEmployeeByUsername(manName);
		
		Division d = new Division();
		d.setName(divName);
		d.setManager(manager);
	

		// todo: set the division for the manager.
		if (manager != null) {
		
		}

		divisionService.saveOrUpdate(d);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

}
