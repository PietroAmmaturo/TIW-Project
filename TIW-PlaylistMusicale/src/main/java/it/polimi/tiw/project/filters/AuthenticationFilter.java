package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.beans.User;

import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = { "/GoToPlaylist", "/RemoveSongsFromPlaylist", "/AddSongsToPlaylist", "/GoToPlayer",  "/GoToHomeJS" })
@Priority(1)
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // TODO: set create to false after authentication is done
        HttpSession session = httpRequest.getSession(true);
        User user = new User();
        user.setId(1);
        // TODO: remove this line after authentication is done
        session.setAttribute("currentUser", user);
        boolean isAuthenticated = session != null && session.getAttribute("currentUser") != null;
        if (isAuthenticated) {
            // User is authenticated, continue processing the request
            chain.doFilter(request, response);
        } else {
            // User is not authenticated, redirect to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Destruction code, if needed
    }
}
