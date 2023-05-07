package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;

@WebFilter(filterName = "SongAuthorizationFilter", urlPatterns = { "/GoToPlayer" })
@Priority(12)
public class SongAuthorizationFilter implements Filter {

    private Connection connection;

    public void init(FilterConfig config) throws ServletException {
        try {
            ServletContext context = config.getServletContext();
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

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        int userId = ((User) session.getAttribute("currentUser")).getId();
        int songId;
        try{
        	songId = Integer.parseInt(request.getParameter("songId"));
        }catch (NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songId' is missing");
			return;
        }
        catch (NumberFormatException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songId' must be a valid integer");
			return;
        }
        catch (Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songId' must be a valid integer");
			return;
        }
        SongDAO songDAO = new SongDAO(connection);
		Boolean found;
		try {
			found = songDAO.doesSongBelongToUser(songId, userId);
		} catch (SQLException e) {
			e.printStackTrace();
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
			return;
		}	
		if(!found) {
			// Purposely not differentiating between song not belonging to user and song not existing
			// to avoid attacker being able to use that information to infer the existence of a song with a specific ID
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Couldn't find a song with the provided ID that belongs to your account");
			return;
		}
        chain.doFilter(request, response);
    }
    
    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
