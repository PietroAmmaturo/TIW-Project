package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Year;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.project.controllers.FileHandler;



/**
 * controlla che i parametri dell'album siano validi
 */
@WebFilter(filterName = "NewAlbumValidationFilter", urlPatterns = { "/AddSongNewAlbum" })
@Priority(12)
public class NewAlbumValidationFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String albumTitle = null;
        String albumArtist = null;
        Part albumCover = null;
        Integer albumYear = null;

        try {
        	albumTitle = StringEscapeUtils.escapeJava(request.getParameter("album_title"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album title is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album title is not valid");
			return;
        }finally {
        	if(albumTitle.isBlank() || albumTitle.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album title cannot be blank");
    			return;
        	}
        }
        
        try {
        	albumArtist = StringEscapeUtils.escapeJava(request.getParameter("album_artist"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The artist is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The artist is not valid");
			return;
        }finally {
        	if(albumArtist.isBlank() || albumArtist.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The artist cannot be blank");
    			return;
        	}
        }
        
        try {
        	albumCover = ((HttpServletRequest) request).getPart("album_cover");
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album cover is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album cover is not valid");
			return;
        }finally {
        	String imageExtension = FileHandler.getFileExtension(albumCover);
        	if(albumCover == null) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album cover cannot be null");
    			return;
        	}
        	if(imageExtension.isBlank() || imageExtension.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album cover extension cannot be blank");
    			return;
        	}
        	if(!imageExtension.equals("jpeg") && !imageExtension.equals("jpg") && !imageExtension.equals("png") &&
        	   !imageExtension.equals("gif") && !imageExtension.equals("bmp") && !imageExtension.equals("tiff") &&
        	   !imageExtension.equals("tif") && !imageExtension.equals("svg") && !imageExtension.equals("webp")) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album cover extension is not supported");
    			return;
        	}
        }
        
        try {
        	albumYear = Integer.parseInt(request.getParameter("album_year"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album year is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album year is not valid");
			return;
        }finally {
        	Year currentYear = Year.now();
        	if(albumYear > currentYear.getValue()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The album has not been published yet");
    			return;
        	}
        }

        chain.doFilter(request, response);
    }
    
    public void destroy() {

    }
}
