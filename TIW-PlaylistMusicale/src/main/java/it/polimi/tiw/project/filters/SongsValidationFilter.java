package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;

@WebFilter(filterName = "SongsValidationFilter", urlPatterns = { "/RemoveSongsFromPlaylist", "/AddSongsToPlaylist", "/AddPlaylist" })
@Priority(21)
public class SongsValidationFilter implements Filter {

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

        String referringPage = httpRequest.getHeader("Referer");
        
        int index = referringPage.indexOf(request.getServletContext().getContextPath());
        if (index != -1) {
            String result = referringPage.substring(index + request.getServletContext().getContextPath().length());
            referringPage = result;
        } else {
        	referringPage = null;
        }
        
        HttpSession session = httpRequest.getSession(false);
        int userId = ((User) session.getAttribute("currentUser")).getId();
        Set<Integer> songIds;
        try{
        	songIds = Arrays.stream(request.getParameterValues("songIds"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }catch (NullPointerException e) {
        	String error = "The parameter 'songIds' is missing, probably you have not selected any songs";
            if (referringPage == null) {
            	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
        		return;
            }
            httpRequest.getSession(false).setAttribute("error", error);
    		String path = request.getServletContext().getContextPath() + referringPage;
    		httpResponse.sendRedirect(path);
			return;
        }
        catch (NumberFormatException e) {
        	String error = "Some 'songIds' parameters are not a valid integer";
            if (referringPage == null) {
            	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
        		return;
            }
            httpRequest.getSession(false).setAttribute("error", error);
    		String path = request.getServletContext().getContextPath() + referringPage;
    		httpResponse.sendRedirect(path);
			return;
        }
        catch (Exception e) {
        	String error = "The parameter 'songIds' is wrongly formatted";
            if (referringPage == null) {
            	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, error);
        		return;
            }
            httpRequest.getSession(false).setAttribute("error", error);
    		String path = request.getServletContext().getContextPath() + referringPage;
    		httpResponse.sendRedirect(path);
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
