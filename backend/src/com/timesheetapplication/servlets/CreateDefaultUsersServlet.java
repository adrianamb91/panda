package com.timesheetapplication.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.timesheetapplication.enums.Roles;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.EmployeeService;
import com.timesheetapplication.service.ProjectService;

@WebServlet("/create-default-users")
public class CreateDefaultUsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	EmployeeService employeeService = new EmployeeService();

	ProjectService projectService = new ProjectService();

	public CreateDefaultUsersServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<h2>Creating default users<h2>");

		// Employee e;
		//
		// if (employeeService.findEmployeeByUsername("abogza") == null) {
		// e = new Employee();
		// e.setUsername("abogza");
		// e.setPassword("admin");
		// e.setFirstName("Adriana");
		// e.setLastName("Bogza");
		// e.setJob(Roles.ADMIN.name());
		// e.setEmail("abogza@isi.com");
		//
		// employeeService.saveOrUpdate(e);
		// }
		//
		// if (employeeService.findEmployeeByUsername("cdinca") == null) {
		// e = new Employee();
		// e.setUsername("cdinca");
		// e.setPassword("clerk");
		// e.setFirstName("Catalin");
		// e.setLastName("Dinca");
		// e.setJob(Roles.CLERK.name());
		// e.setEmail("cdinca@isi.com");
		//
		// employeeService.saveOrUpdate(e);
		// }
		//
		// if (employeeService.findEmployeeByUsername("dmorar") == null) {
		// e = new Employee();
		// e.setUsername("dmorar");
		// e.setPassword("manager");
		// e.setFirstName("Dragos");
		// e.setLastName("Morar");
		// e.setJob(Roles.DEPT_MANAGER.name());
		// e.setEmail("dmorar@isi.com");
		//
		// employeeService.saveOrUpdate(e);
		// }

		out.println("<h2>Adding Projects to database<h2>");

		Employee emp1;
		Employee emp2;
		Project p;

		p = projectService.findProjectByName("Intel");
		if (p == null) {
			emp1 = employeeService.findEmployeeByUsername("cdinca");
			
			p = new Project();
			p.setName("Intel");
			p.getEmployees().add(emp1);
			
			emp2 = employeeService.findEmployeeByUsername("dmorar");
			p.getEmployees().add(emp2);
			
			projectService.saveOrUpdate(p);
			
			emp1.getProjects().add(p);
			employeeService.saveOrUpdate(emp1);
			
			emp2.getProjects().add(p);
			employeeService.saveOrUpdate(emp2);

		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
