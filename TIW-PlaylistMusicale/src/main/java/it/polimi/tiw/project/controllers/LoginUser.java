package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.User;

@WebServlet("/LoginUser")
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public LoginUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}

	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String username = null;
		String password = null;
		boolean valid = true;
		
		try {
			username = request.getParameter("username");
			password = request.getParameter("password");
			
			if(username.isBlank() || username.isEmpty() || password.isBlank() || password.isEmpty())
				valid = false;
		}catch(NullPointerException e) {
			valid = false;
		}
		
		if(!valid) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		
		UserDAO userDao = new UserDAO(connection);
		boolean usernameUsed;
		try {
			usernameUsed = userDao.usernameAlreadyInUse(username);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing or incorrect parameters");
			return;
		}
		
		try {
			if(usernameUsed) {
				User user = userDao.findUserByUsername(username);
				if(password.equals(user.getPassword())) {
					//accedi
					HttpSession session = request.getSession(true);
			        session.setAttribute("currentUser", user);
			        String path = getServletContext().getContextPath() + "/GoToHome";
					response.sendRedirect(path);
				}
				else {
					// password non corretta
					 request.setAttribute("errorMessage", "Invalid username or password");
			         RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Login.html");
			         dispatcher.forward(request, response);
				}
			} else {
				//TODO utente non esistente
				request.setAttribute("errorMessage", "Invalid username or password");
		        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/Login.html");
		        dispatcher.forward(request, response);
			}
		}catch(Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in login");
			return;
		}
	}
}