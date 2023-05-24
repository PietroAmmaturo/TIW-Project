package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

@WebFilter(filterName = "SongsValidationFilter", urlPatterns = { "/RemoveSongsFromPlaylist", "/AddSongsToPlaylist" })
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

        HttpSession session = httpRequest.getSession(false);
        int userId = ((User) session.getAttribute("currentUser")).getId();
        Set<Integer> songIds;
        try{
        	songIds = Arrays.stream(request.getParameterValues("songIds"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }catch (NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songIds' is missing");
			return;
        }
        catch (NumberFormatException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Some elements inside the parameter 'songIds' are not a valid integer");
			return;
        }
        catch (ArrayIndexOutOfBoundsException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songIds' is an empty string");
			return;
        }
        catch (IllegalArgumentException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songIds' contains more than one comma in a row or it contains whitespace");
			return;
        } catch (Exception e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The parameter 'songIds' must be a valid array of integers");
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
