package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;



/**
 * controlla che lo username e la password inseriti in fase di login non siano vuoti
 */
@WebFilter(filterName = "UserValidationFilter", urlPatterns = { "/LoginUser", "/RegisterUser" })
@Priority(12)
public class UserValidationFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String username = null;
        String password = null;

        try {
        	username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The username is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The username is not valid");
			return;
        }finally {
        	if(username.isBlank() || username.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The username cannot be blank");
    			return;
        	}
        }
        
        try {
        	password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The password is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The password is not valid");
			return;
        }finally {
        	if(password.isBlank() || password.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The password cannot be blank");
    			return;
        	}
        }

        chain.doFilter(request, response);
    }
    
    public void destroy() {

    }
}
