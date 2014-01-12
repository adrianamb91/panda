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
import org.json.JSONException;
import org.json.JSONObject;

import com.timesheetapplication.model.Department;
import com.timesheetapplication.model.Employee;
import com.timesheetapplication.service.DepartmentService;
import com.timesheetapplication.service.EmployeeService;

/**
 * Servlet implementation class DepartmentManagerServlet
 */
@WebServlet("/DepartmentManagerServlet")
public class DepartmentManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	HttpSession session; 
	DepartmentService departmentService = new DepartmentService();
	EmployeeService employeeService = new EmployeeService();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DepartmentManagerServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String phase = new String(request.getParameter("phase").toString());
		JSONObject responseMessage = new JSONObject();
		
		session = request.getSession();
		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");

		switch (phase) {
		case "loadEmployees":
			loadEmployees(loggedInUser, responseMessage, response);
			break;
		default:
			break;
		}

	}

	private void loadEmployees(Employee loggedInUser, JSONObject responseMessage, HttpServletResponse response) {
		// TODO Auto-generated method stub
		Department d = departmentService.findDepartmentByManager(loggedInUser);
		List<Employee> employees = null;
		ArrayList<String> employeesFLN = null;
		if (d != null) {
			employees = employeeService.findAllEmployeesByDepartment(d);
			employeesFLN = new ArrayList<String>();
			for (Employee e: employees) {
				employeesFLN.add(e.getFirstName() + " " + e.getLastName()); 
			}
		}
		try {
			if (employeesFLN != null) {
				responseMessage.put("ok", true);
				JSONArray emps = new JSONArray(employeesFLN);
				//System.out.println(emps.toString());
				responseMessage.put("elements", emps);
				responseMessage.put("size", employeesFLN.size());
			}
			else {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
