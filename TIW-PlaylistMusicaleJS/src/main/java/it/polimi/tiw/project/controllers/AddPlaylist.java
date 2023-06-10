/*controllare che tutte le canzone passate esistano, siano dell'utente
 *crea la playlist e chiama addSongsToPlaylist */
 
package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.DAO.SongPlaylistDAO;
import it.polimi.tiw.project.beans.User;

@WebServlet("/AddPlaylist")
public class AddPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddPlaylist() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		Integer userId = ((User) session.getAttribute("currentUser")).getId();
		
		String playlistTitle = null;
		String playlistDescription = null;
		boolean valid = true;
		List<Integer> songIds = new ArrayList<>();
		String[] selectedSongIds = null;
		
		try {
			playlistTitle = request.getParameter("playlist_title");
			playlistDescription = request.getParameter("playlist_description");
			selectedSongIds = request.getParameterValues("songIds");
			if(playlistTitle.isBlank() || playlistTitle.isEmpty() || playlistDescription.isEmpty())
				valid = false;
		}catch(NullPointerException e) {
			valid = false;
		}
		
		if(!valid) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		
		boolean allSongsPresent = true;
		
		//mettere if l'utente è valido
		if (selectedSongIds != null) {
	        // Convert each selected song ID from string to integer and add it to the ArrayList
	        for (String songId : selectedSongIds) {
	            songIds.add(Integer.parseInt(songId));
	            /*if(una canzone non esiste)
	            	allSongsPresent = false;*/
	        }
	    }
		
		PlaylistDAO playlistDao = new PlaylistDAO(connection);
		//mettere if tutte le canzoni esistono
		try {
			if(playlistDao.playlistTitleUsed(playlistTitle, userId)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The title for the playlist is already in use");
				return;
			}else {
				int playlistId = playlistDao.addPlaylist(playlistTitle, playlistDescription, userId);
				// Forward the request to another servlet
				String path = "/AddSongsToPlaylist?playlistId=" + playlistId;
			    RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			    dispatcher.forward(request, response);
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "SQL exception");
			e.printStackTrace();
			return;
		}
		
		
	
	}
}