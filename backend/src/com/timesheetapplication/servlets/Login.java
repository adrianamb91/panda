package com.timesheetapplication.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.timesheetapplication.model.Employee;
import com.timesheetapplication.service.EmployeeService;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private EmployeeService employeeService = new EmployeeService();

	public Login() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		JSONObject responseMessage = new JSONObject();

		System.out.println("---post request---");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("Username: " + username + "\nPassword: " + password);

		Integer accessGranted = employeeService.checkUsernameForAccess(
				username, password);
		System.out.println("access= " + accessGranted);

		Boolean isAdmin = (accessGranted == 2);

		Employee loggedInUser = employeeService
				.findEmployeeByUsername(username);

		if (loggedInUser != null) {
			HttpSession session = request.getSession();
			session.setAttribute("loggedInUser", loggedInUser);
			try {
				responseMessage.put("job", loggedInUser.getJob());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			switch (accessGranted) {
			case -1:
				responseMessage.put("access", "wrong_user");
				break;
			case -2:
				responseMessage.put("access", "wrong_password");
				break;
			default:
				responseMessage.put("access", "granted");
				break;
			}
			responseMessage.put("isAdmin", isAdmin);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		response.setContentType("application/json; charset=UTF-8");
		response.getWriter().write(responseMessage.toString());
		System.out.println("---/post request---");
	}
}
