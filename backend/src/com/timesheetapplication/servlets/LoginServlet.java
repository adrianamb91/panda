package com.timesheetapplication.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
@RequestMapping("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	//GenericDao<Clerk> dao = new GenericDaoImpl<Clerk>();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    
	public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("Username: " + username + "\nPassword: " + password);
		
		//TODO: check if username/password is valid and send response message
		
		//Employee e = new Clerk();
		//e.setFirstName(username);
		//e.setLastName(username);
		
		//dao.saveOrUpdate(e);
	
		//System.out.println("presumably persisted");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
