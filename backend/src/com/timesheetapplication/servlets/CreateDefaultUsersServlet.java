package com.timesheetapplication.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.timesheetapplication.enums.Job;
import com.timesheetapplication.model.Client;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.ClientService;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.DivisionService;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.ProjectService;

@WebServlet("/create-default-users")
public class CreateDefaultUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	EmployeeService employeeService = new EmployeeService();

	ProjectService projectService = new ProjectService();
	
	DivisionService divisionService = new DivisionService();
	
	DepartmentService departmentService = new DepartmentService();
	
	ClientService clientService = new ClientService();

	public CreateDefaultUsersServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<h2>Creating default users<h2>");

		Employee e = employeeService.findEmployeeByUsername("tbasescu");
		if (e == null) {
			e = new Employee();
			e.setUsername("tbasescu");
			e.setJob(Job.DIVISION_MANAGER);
			e.setPassword("divmanager");
			employeeService.saveOrUpdate(e);
		}
		
		if (employeeService.findEmployeeByUsername("abogza") == null) {
			e = new Employee();
			e.setUsername("abogza");
			e.setPassword("admin");
			e.setFirstName("Adriana");
			e.setLastName("Bogza");
			e.setJob(Job.ADMIN);
			e.setEmail("abogza@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("cdinca") == null) {
			e = new Employee();
			e.setUsername("cdinca");
			e.setPassword("clerk");
			e.setFirstName("Catalin");
			e.setLastName("Dinca");
			e.setJob(Job.CLERK);
			e.setEmail("cdinca@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("dmoraru") == null) {
			e = new Employee();
			e.setUsername("man");
			e.setPassword("manager");
			e.setFirstName("c");
			e.setLastName("d");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("cd@isi.com");

			employeeService.saveOrUpdate(e);
		}
		
		
		e = employeeService.findEmployeeByUsername("tbasescu");
		/*
		out.println("<h2>Adding Projects to database<h2>");

		// add a default division:
		Division div = divisionService.findDivisionByName("IT");
		if (div == null) {
			div = new Division();
			div.setName("IT");
		}
		div.setManager(e);
		divisionService.saveOrUpdate(div);
		
		Department dep = departmentService.findDepartmentByName("DEZVOLTARE SOFTWARE");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DS");
			dep.setDivision(div);
			dep.setManager(e);
			dep.setName("DEZVOLTARE SOFTWARE");
			departmentService.saveOrUpdate(dep);
		}
		e = employeeService.findEmployeeByUsername("dmoraru");
		e.setDepartment(dep);
		employeeService.saveOrUpdate(e);
		
		Client c = clientService.findClientByName("VODAFONE");
		if (c == null) {
			c = new Client();
			c.setName("VODAFONE");
		}
		clientService.saveOrUpdate(c);
		
		// add project
		Project p;
		p = projectService.findProjectByName("CORPORATE");
		if (p == null) {
			p = new Project();
			p.setDepartment(dep);
			p.setName("CORPORATE");
			p.setClient(c);
		}
		projectService.saveOrUpdate(p);
		c.getProjects().add(p);
		clientService.saveOrUpdate(c);
		*/
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
