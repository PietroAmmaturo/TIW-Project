package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
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

import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.DAO.SongPlaylistDAO;
import it.polimi.tiw.project.beans.User;

@WebServlet("/AddSongsToPlaylist")
public class AddSongsToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddSongsToPlaylist() {
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
		int userId = ((User) session.getAttribute("currentUser")).getId();
    	int playlistId = Integer.parseInt(request.getParameter("playlistId"));
    	Set<Integer> songIds = Arrays.stream(request.getParameterValues("songIds"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    	Set<Integer> validSongIds = new HashSet();
        Boolean foundInvalidId = false;
        SongDAO songDAO = new SongDAO(connection);
		SongPlaylistDAO songPlaylistDAO = new SongPlaylistDAO(connection);
		Boolean foundUser;
		Boolean foundPlaylist;
		
		// guaranteeing expected behavior for valid songs only
		for (Integer songId : songIds) {
			// preventing malicious user from adding someone else's songs to its playlist
			try {
				foundUser = songDAO.doesSongBelongToUser(songId, userId);
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed.");
				return;
			}
			if(!foundUser) {
				foundInvalidId = true;
				continue;
			}
			// preventing malicious user from adding the same song multiple times to a playlist
			try {
				foundPlaylist = songPlaylistDAO.doesSongBelongToPlaylist(songId, playlistId);
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed.");
				return;
			}
			if(foundPlaylist) {
				foundInvalidId = true;
				continue;
			}
			validSongIds.add(songId);
		}
		try {
			songPlaylistDAO.addSongsToPlaylist(validSongIds, playlistId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed.");
			return;
		}
		if (foundInvalidId) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Some of the selected songs could not be found, did not belong to the user  or were already in the playlist, the valid songs were added successfully.");
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