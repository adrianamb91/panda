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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<h2>Creating default users<h2>");

		Employee e = employeeService.findEmployeeByUsername("tbasescu");
		if (e == null) {
			e = new Employee();
			e.setUsername("tbasescu");
			e.setFirstName("Traian");
			e.setLastName("Basescu");
			e.setEmail("tbasescu@isi.com");
			e.setJob(Job.DIVISION_MANAGER);
			e.setPassword("divmanager");
			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("iiliescu") == null) {
			e = new Employee();
			e.setUsername("iiliescu");
			e.setPassword("divmanager");
			e.setFirstName("Ion");
			e.setLastName("Iliescu");
			e.setJob(Job.DIVISION_MANAGER);
			e.setEmail("iiliescu@isi.com");

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
			e.setUsername("dmoraru");
			e.setPassword("manager");
			e.setFirstName("Dragos");
			e.setLastName("Moraru");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("dmoraru@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("spopescu") == null) {
			e = new Employee();
			e.setUsername("spopescu");
			e.setPassword("manager");
			e.setFirstName("Stela");
			e.setLastName("Popescu");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("spopescu@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("dtrump") == null) {
			e = new Employee();
			e.setUsername("dtrump");
			e.setPassword("manager");
			e.setFirstName("Donald");
			e.setLastName("Trump");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("dtrump@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("drockefeller") == null) {
			e = new Employee();
			e.setUsername("drockefeller");
			e.setPassword("manager");
			e.setFirstName("David");
			e.setLastName("Rockefeller");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("drockefeller@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("bgates") == null) {
			e = new Employee();
			e.setUsername("bgates");
			e.setPassword("manager");
			e.setFirstName("Bill");
			e.setLastName("Gates");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("bgates@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("hford") == null) {
			e = new Employee();
			e.setUsername("hford");
			e.setPassword("manager");
			e.setFirstName("Henry");
			e.setLastName("Ford");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("hford@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("sjobs") == null) {
			e = new Employee();
			e.setUsername("sjobs");
			e.setPassword("manager");
			e.setFirstName("Steve");
			e.setLastName("Jobs");
			e.setJob(Job.DEPT_MANAGER);
			e.setEmail("sjobs@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("bobama") == null) {
			e = new Employee();
			e.setUsername("bobama");
			e.setPassword("ceo");
			e.setFirstName("Barack");
			e.setLastName("Obama");
			e.setJob(Job.CEO);
			e.setEmail("bobama@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("inastase") == null) {
			e = new Employee();
			e.setUsername("inastase");
			e.setPassword("clerk");
			e.setFirstName("Ilie");
			e.setLastName("Nastase");
			e.setJob(Job.CLERK);
			e.setEmail("inastase@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("gdinica") == null) {
			e = new Employee();
			e.setUsername("gdinica");
			e.setPassword("clerk");
			e.setFirstName("Gheorghe");
			e.setLastName("Dinica");
			e.setJob(Job.CLERK);
			e.setEmail("gdinica@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("mgandhi") == null) {
			e = new Employee();
			e.setUsername("mghandi");
			e.setPassword("clerk");
			e.setFirstName("Mahatma");
			e.setLastName("Gandhi");
			e.setJob(Job.CLERK);
			e.setEmail("mgandhi@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("mmonroe") == null) {
			e = new Employee();
			e.setUsername("mmonroe");
			e.setPassword("clerk");
			e.setFirstName("Marilyn");
			e.setLastName("Monroe");
			e.setJob(Job.CLERK);
			e.setEmail("mmonroe@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("cchanel") == null) {
			e = new Employee();
			e.setUsername("cchanel");
			e.setPassword("clerk");
			e.setFirstName("Coco");
			e.setLastName("Chanel");
			e.setJob(Job.CLERK);
			e.setEmail("cchanel@isi.com");

			employeeService.saveOrUpdate(e);
		}

		if (employeeService.findEmployeeByUsername("mthatcher") == null) {
			e = new Employee();
			e.setUsername("mthatcher");
			e.setPassword("clerk");
			e.setFirstName("Margaret");
			e.setLastName("Thatcher");
			e.setJob(Job.CLERK);
			e.setEmail("mthathcer@isi.com");

			employeeService.saveOrUpdate(e);
		}

		out.println("<h2>Adding Projects to database<h2>");

		// add a default division:
		Division div = divisionService.findDivisionByName("IT");
		if (div == null) {
			div = new Division();
			div.setName("IT");

			e = employeeService.findEmployeeByUsername("tbasescu");

			div.setManager(e);
			divisionService.saveOrUpdate(div);
		}

		Division div2 = divisionService.findDivisionByName("MHF");
		if (div2 == null) {
			div2 = new Division();
			div2.setName("MHF");

			e = employeeService.findEmployeeByUsername("iiliescu");

			div.setManager(e);
			divisionService.saveOrUpdate(div2);
		}

		Department dep = departmentService
				.findDepartmentByName("Dezvoltare Software");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DDS");
			dep.setDivision(div);
			e = employeeService.findEmployeeByUsername("bgates");
			dep.setManager(e);
			dep.setName("Dezvoltare Software");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Consultanta");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DC");
			dep.setDivision(div);
			e = employeeService.findEmployeeByUsername("drockefeller");
			dep.setManager(e);
			dep.setName("Consultanta");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Service");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DS");
			dep.setDivision(div);
			e = employeeService.findEmployeeByUsername("hford");
			dep.setManager(e);
			dep.setName("Service");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Asamblare Calculatoare");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DA");
			dep.setDivision(div);
			e = employeeService.findEmployeeByUsername("sjobs");
			dep.setManager(e);
			dep.setName("Asamblare Calculatoare");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Marketing");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DM");
			dep.setDivision(div2);
			e = employeeService.findEmployeeByUsername("dmoraru");
			dep.setManager(e);
			dep.setName("Marketing");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Resurse Umane");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DHR");
			dep.setDivision(div2);
			e = employeeService.findEmployeeByUsername("spopescu");
			dep.setManager(e);
			dep.setName("Resurse Umane");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		dep = departmentService.findDepartmentByName("Financiar");
		if (dep == null) {
			dep = new Department();
			dep.setAbbv("DF");
			dep.setDivision(div2);
			e = employeeService.findEmployeeByUsername("dtrump");
			dep.setManager(e);
			dep.setName("Financiar");
			departmentService.saveOrUpdate(dep);

			e.setDepartment(dep);
			employeeService.saveOrUpdate(e);
		}

		/*
		 * Client c = clientService.findClientByName("VODAFONE"); if (c == null)
		 * { c = new Client(); c.setName("VODAFONE"); }
		 * clientService.saveOrUpdate(c);
		 * 
		 * // add project Project p; p =
		 * projectService.findProjectByName("CORPORATE"); if (p == null) { p =
		 * new Project(); p.setDepartment(dep); p.setName("CORPORATE");
		 * p.setClient(c); } projectService.saveOrUpdate(p);
		 * c.getProjects().add(p); clientService.saveOrUpdate(c);
		 */
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}
}