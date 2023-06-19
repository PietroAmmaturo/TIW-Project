package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.DAO.SongPlaylistDAO;
import it.polimi.tiw.project.beans.User;

@WebServlet("/RemoveSongsFromPlaylist")
public class RemoveSongsFromPlaylist extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RemoveSongsFromPlaylist() {
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
    	int playlistId = Integer.parseInt(request.getParameter("playlistId"));
    	
    	Set<Integer> songIds = Arrays.stream(request.getParameterValues("songIds"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    	Set<Integer> validSongIds = new HashSet();
        Boolean foundInvalidId = false;
		SongPlaylistDAO songPlaylistDAO = new SongPlaylistDAO(connection);
		Boolean foundPlaylist;
		
		// guaranteeing expected behavior for valid songs only
		for (Integer songId : songIds) {
			// preventing user from deleting a song already not belonging to playlist
			try {
				foundPlaylist = songPlaylistDAO.doesSongBelongToPlaylist(songId, playlistId);
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed.");
				return;
			}
			if(!foundPlaylist) {
				foundInvalidId = true;
				continue;
			}
			validSongIds.add(songId);
		}
		try {
			songPlaylistDAO.removeSongsFromPlaylist(validSongIds, playlistId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed.");
			return;
		}
		if (foundInvalidId) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Some of the selected songs were not found in the playlist. The remaining valid songs were successfully removed.");
			return;
		}
	}


	@Override
	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				
			}
		}
	}
}
