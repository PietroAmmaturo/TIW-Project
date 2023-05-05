package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

@WebServlet("/CreateSongPlaylist")
public class CreateSongPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateSongPlaylist() {
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
    	int songId = Integer.parseInt(request.getParameter("songId"));
    	int playlistId = Integer.parseInt(request.getParameter("playlistId"));
		// a different design (having UserId in the SongPlaylist table as example), might have allowed for us to do all 3 checks at the same time (one query)
		// this would have prevented the user from knowing the reason why the operation wasn't successful though, which is not ideal,
		// unless that information could be used to infer other informations, but in this case an attacker would not be able to infer any information from the given responses
		// the only informations given are related to his personal account
        SongPlaylistDAO songPlaylistDAO = new SongPlaylistDAO(connection);
		Boolean found;
		try {
			found = songPlaylistDAO.doesSongBelongToPlaylist(songId, playlistId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
			return;
		}	
		if(found) {
			response.sendError(HttpServletResponse.SC_CONFLICT, "Song is already in playlist");
			return;
		}
		String path = getServletContext().getContextPath() + "/GoToPlaylist?playlistId=" + playlistId;
		response.sendRedirect(path);
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