package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

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

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.beans.Project;
import it.polimi.tiw.projects.dao.AdminDAO;
import it.polimi.tiw.projects.dao.ProjectDAO;

@WebServlet("/GoToProjectManagement")
public class GoToProjectManagement extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GoToProjectManagement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession s = request.getSession();
		User u = (User) s.getAttribute("user");
		String chosenProject = request.getParameter("projectid");
		AdminDAO aDao = new AdminDAO(connection, u.getId());
		List<Project> projects = new ArrayList<Project>();
		List<User> workers = new ArrayList<User>();
		int chosenProjectId = 0;
		try {
			projects = aDao.findProjects();
			if (chosenProject == null) {
				chosenProjectId = aDao.findDefaultProject();
			} else {
				chosenProjectId = Integer.parseInt(chosenProject);
			}
			ProjectDAO pDao = new ProjectDAO(connection, chosenProjectId);
			if(pDao.findProject() == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with project choice");
				return;
			}
			User owner = pDao.findOwner();
			if(owner == null || owner.getId() != u.getId()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to access non-owned project");
				return;
			}
			workers = pDao.findWorkers();
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in admin's project database extraction");
		}
		String path = "/WEB-INF/ProjectManagement.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("projects", projects);
		ctx.setVariable("projectid", chosenProjectId);
		ctx.setVariable("workers", workers);
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}
