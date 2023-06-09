package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.project.DAO.AlbumDAO;
import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Playlist;
import it.polimi.tiw.project.beans.Song;
import it.polimi.tiw.project.beans.User;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GetHome")
public class GetHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetHome() {
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

		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Redirect to the Home page and add missions to the parameters
		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("currentUser")).getId();
		//se serve l'id dell'utente è questo
		
		AlbumDAO albumDao = new AlbumDAO(connection);
		PlaylistDAO playlistDao = new PlaylistDAO(connection);
		SongDAO songDao = new SongDAO(connection);
		List<Album> albums;
		List<Playlist> playlists;
		List<Song> songs;
		try {
	        albums = albumDao.findAlbumsByUserId(userId);
			playlists = playlistDao.findPlaylistsByUserId(userId);
			songs = songDao.findAllSongsByUserId(userId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
	        return;
	    }
		
		Gson gson = new GsonBuilder()
	    		.setLenient()
                .create();
	
		 // Convert objects to JSON strings
	    String songsSerialized = gson.toJson(songs);
	    String playlistsSerialized = gson.toJson(playlists);
	    String albumsSerialized = gson.toJson(albums);
	
		 // Set the response content type to JSON
		 response.setContentType("application/json");
	
		 // Write the response JSON string to the response output stream
		 response.getWriter().write(
				 "{\"songs\":" + songsSerialized + "," +
				 "\"albums\":" + albumsSerialized + "," +
				 "\"playlists\":" + albumsSerialized + "}"
				 );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}