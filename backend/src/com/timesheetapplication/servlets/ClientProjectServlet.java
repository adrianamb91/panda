package com.timesheetapplication.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.timesheetapplication.model.Client;
import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Division;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.model.Project;
import com.timesheetapplication.service.ClientService;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.ProjectService;
import com.timesheetapplication.utils.TSMUtil;

/**
 * Servlet implementation class ClientProjectServlet
 */
@WebServlet("/ClientProjectServlet")
public class ClientProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ProjectService projectService = new ProjectService();
	private ClientService clientService = new ClientService();
	private DepartmentService departmentService = new DepartmentService();
	
	HttpSession session;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientProjectServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();
		
		session = request.getSession();
		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		switch (phase) {
		case "loadAllClients":
			processLoadAllClients(responseMessage, response);
			break;
		case "saveClient":
			processSaveClient(request, responseMessage, response);
			break;
		case "getDepartment":
			processGetDepartment(loggedInUser, request, responseMessage, response);
			break;
		case "saveProject":
			processSaveProject(request, responseMessage, response);
			break;
		case "loadAllProjects":
			processLoadAllProjects(loggedInUser, responseMessage, response);
			break;
		default:
			break;
		}
	}

	private void processLoadAllProjects(Employee loggedInUser, JSONObject responseMessage,
			HttpServletResponse response) {
		Department d = loggedInUser.getDepartment();
		List<Project> projects = projectService.getProjectsForDepartment(d);
		ArrayList<String> projectNames = new ArrayList<String>();
		ArrayList<String> clientNames = new ArrayList<String>();
		ArrayList<String> departmentNames = new ArrayList<String>();
		
		for (Project p: projects) {
			if (p.getClient() != null && p.getDepartment() != null) {
				projectNames.add(p.getName());
				clientNames.add(p.getClient().getName());
				departmentNames.add(p.getDepartment().getName());
			}
		}
		
		try {
			responseMessage.put("ok", true);
			JSONArray projs = new JSONArray(projectNames);
			JSONArray clients = new JSONArray(clientNames);
			JSONArray departments = new JSONArray(departmentNames);

			responseMessage.put("elements", projs);
			responseMessage.put("clients", clients);
			responseMessage.put("departments", departments);
			responseMessage.put("size", projectNames.size());

			System.out.println(projs.toString());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveProject(HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		String clientName = request.getParameter("client");
		String projectName = request.getParameter("project");
		String departmentName = request.getParameter("department");
		
		Department d = departmentService.findDepartmentByName(departmentName);
		Client c = clientService.findClientByName(clientName);
		
		try {
			if (d != null && c != null) {
				Project p = new Project();
				p.setClient(c);
				p.setDepartment(d);
				p.setName(projectName);
				
				projectService.saveOrUpdate(p);
				responseMessage.put("ok", true);
			} else {
				responseMessage.put("ok", false);
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void processGetDepartment(Employee loggedInUser, HttpServletRequest request,
			JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Department d = departmentService.findDepartmentByManager(loggedInUser);
		
		try {
			if (d == null) {
				responseMessage.put("ok", false);
			} else {
				responseMessage.put("ok", true);
				responseMessage.put("deptName", d.getName());
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processSaveClient(HttpServletRequest request, JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String clientName = request.getParameter("name");

		System.out.println("request: " + clientName);

		Client d = new Client();
		d.setName(clientName);

		clientService.saveOrUpdate(d);

		try {
			responseMessage.put("ok", true);
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processLoadAllClients(JSONObject responseMessage,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<Client> clients = clientService.loadAll();
		ArrayList<String> clientNames = new ArrayList<String>();

		for (Client c : clients) {
			clientNames.add(c.getName());
		}
		
		try {
			responseMessage.put("ok", true);
			JSONArray names = new JSONArray(clientNames);

			responseMessage.put("elements", names);
			responseMessage.put("size", clients.size());

			System.out.println(names.toString());

			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
