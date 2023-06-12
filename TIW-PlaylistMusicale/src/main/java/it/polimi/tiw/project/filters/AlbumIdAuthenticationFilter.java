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

import it.polimi.tiw.project.DAO.AlbumDAO;
import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;

/**
 * controlla che l'album id sia di un album appartenente all'utente in sessione
 */
@WebFilter(filterName = "AlbumIdAuthenticationFilter", urlPatterns = { "/AddSongExistingAlbum" })
@Priority(12)
public class AlbumIdAuthenticationFilter implements Filter {

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
        int albumId;
        
        try {
        	albumId = Integer.parseInt(request.getParameter("albumId"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album id is null, not valid");
			return;
        }catch(NumberFormatException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album id format is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album id is not valid");
			return;
        }
       
        AlbumDAO albumDao = new AlbumDAO(connection);
        boolean belong = false;
        
        try {
        	belong = albumDao.albumBelongToUser(albumId, userId);
        }catch(SQLException e) {
        	httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in reaching the database");
			return;
        }finally {
        	if(!belong) {
            	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album id doesn't belong to the current user or doesn't exists");
    			return;
        	}
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
