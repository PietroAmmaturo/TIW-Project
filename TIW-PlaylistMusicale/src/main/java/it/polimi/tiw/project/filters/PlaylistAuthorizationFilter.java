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

import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;

@WebFilter(filterName = "PlaylistAuthorizationFilter", urlPatterns = { "/GoToPlaylist", "/RemoveSongsFromPlaylist", "/AddSongsToPlaylist", "/GoToHomeJS" })
@Priority(11)
public class PlaylistAuthorizationFilter implements Filter {

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
        int playlistId;
        try{
        	playlistId = Integer.parseInt(request.getParameter("playlistId"));
        }catch (NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'playlistId' is missing");
			return;
        }
        catch (NumberFormatException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'playlistId' must be a valid integer");
			return;
        }
        catch (Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'playlistId' must be a valid integer");
			return;
        }
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
		Boolean found;
		try {
			found = playlistDAO.doesPlaylistBelongToUser(playlistId, userId);
		} catch (SQLException e) {
			e.printStackTrace();
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
			return;
		}	
		if(!found) {
			// Purposely not differentiating between playlist not belonging to user and playlist not existing
			// to avoid attacker being able to use that information to infer the existence of a playlist with a specific ID
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Couldn't find a playlist with the provided ID that belongs to your account");
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
