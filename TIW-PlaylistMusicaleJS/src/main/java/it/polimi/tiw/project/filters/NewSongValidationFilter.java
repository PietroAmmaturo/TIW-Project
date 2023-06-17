package it.polimi.tiw.project.filters;

import javax.annotation.Priority;
import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

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
import it.polimi.tiw.project.utils.FileService;



/**
 * controlla che i parametri della canzone siano validi
 */
@WebFilter(filterName = "NewSongValidationFilter", urlPatterns = { "/AddSongExistingAlbum", "/AddSongNewAlbum" })
@Priority(12)
public class NewSongValidationFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String songTitle = null;
        String songGenre = null;
        Part audioFile = null;
        String audioFileExtension;

        try {
        	songTitle = StringEscapeUtils.escapeJava(request.getParameter("song_title"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song title is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song title is not valid");
			return;
        }finally {
        	if(songTitle.isBlank() || songTitle.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song title cannot be blank");
    			return;
        	}
        }
        
        try {
        	songGenre = StringEscapeUtils.escapeJava(request.getParameter("song_genre"));
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song genre is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song genre is not valid");
			return;
        }finally {
        	if(songGenre.isBlank() || songGenre.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The song genre cannot be blank");
    			return;
        	}
        	if(!songGenre.equals("Rock") && !songGenre.equals("Pop") && !songGenre.equals("Country") &&
        	   !songGenre.equals("Indie") && !songGenre.equals("Classical")) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "This song genre is not recognized");
    			return;
        	}
        }
        
        try {
        	audioFile = ((HttpServletRequest) request).getPart("audioFile");
        }catch(NullPointerException e) {
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The audio file is not valid");
			return;
        }catch(Exception e){
        	httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The audio file is not valid");
			return;
        }finally {
        	audioFileExtension = FileService.getFileExtension(audioFile);
        	if(audioFile == null) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The audio file cannot be null");
    			return;
        	}
        	if(audioFileExtension.isBlank() || audioFileExtension.isEmpty()) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The audio file extension cannot be blank");
    			return;
        	}
        	if(!audioFileExtension.equals("mp3") && !audioFileExtension.equals("wav") && !audioFileExtension.equals("aac") &&
        	   !audioFileExtension.equals("flac") && !audioFileExtension.equals("ogg") && !audioFileExtension.equals("m4a") &&
        	   !audioFileExtension.equals("wma")) {
        		httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "The audio file extension is not supported");
    			return;
        	}
        }

        chain.doFilter(request, response);
    }
    
    public void destroy() {

    }
}
