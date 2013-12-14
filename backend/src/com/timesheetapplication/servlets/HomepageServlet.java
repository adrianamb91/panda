package com.timesheetapplication.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import com.timesheetapplication.model.Employee;
import com.timesheetapplication.utils.TSMUtil;

@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	HttpSession session;

	public HomepageServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();

		String phase = new String(request.getParameter("phase").toString());

		System.out.println("homepage servlet knows about user = "
				+ session.getAttribute("loggedInUser"));

		Employee loggedInUser = (Employee) session.getAttribute("loggedInUser");
		if (loggedInUser == null) {
			return;
		}
		

		JSONObject responseMessage = new JSONObject();
		if (phase.equalsIgnoreCase("init")) {
			try {
				responseMessage.put("name", loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
				responseMessage.put("job", loggedInUser.getJob());
				responseMessage.put("date", TSMUtil.formatDate(new Date()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setContentType("application/json; charset=UTF-8");
			response.getWriter().write(responseMessage.toString());
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
