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

import org.apache.commons.lang.StringEscapeUtils;

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

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		
		UserDAO userDao = new UserDAO(connection);
		boolean usernameUsed;
		boolean rightPassword;
		
		HttpSession session = request.getSession(true);
		
		try {
			usernameUsed = userDao.usernameAlreadyInUse(username);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in reaching the database");
			return;
		}
		
		try {
			
			if(usernameUsed) {
				try {
					rightPassword = userDao.passwordUsedByUser(username, password);
				}catch(SQLException e) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in reaching the database");
					return;
				}
				
				if(rightPassword) {
					//accedi
					User user = userDao.findUserByUsername(username);
					
			        session.setAttribute("currentUser", user);
			        
			        String path = getServletContext().getContextPath() + "/GoToHome?playlistId=-1";
					response.sendRedirect(path);
				} else {
					//password sbagliata
					session.setAttribute("error", "Wrong credentials");
			       	String path = getServletContext().getContextPath() + "/GoToLogin";
					response.sendRedirect(path);
					return;
				}
			} else {
				//username non esistente
				session.setAttribute("error", "Wrong credentials");
		       	String path = getServletContext().getContextPath() + "/GoToLogin";
				response.sendRedirect(path);
				return;
			}
		}catch(Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in login");
			return;
		}
	}
}