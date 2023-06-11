package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.DAO.SongDetailsDAO;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Song;
import it.polimi.tiw.project.beans.User;

@WebServlet("/GetPlaylist")
public class GetPlaylist extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPlaylist() {
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
		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("currentUser")).getId();
		int playlistId = Integer.parseInt(request.getParameter("playlistId"));
	    int songsPerBlock = 10000; // preventing users from retrieving (and trying to render) more than 10k songs
		int totalSongs;
	    int currentBlock;
	    // page validation
        try{
        	currentBlock = Integer.parseInt(request.getParameter("playlistBlock"));
        }catch (Exception e) {
        	currentBlock = 1;
        }
		SongDAO songDAO = new SongDAO(connection);
		SongDetailsDAO songDetailsDAO = new SongDetailsDAO(connection);
		LinkedHashMap<Song, Album> playlistSongsWithAlbum;
		List<Song> userSongs;
	    try {
	        totalSongs = songDetailsDAO.countSongsByPlaylistId(playlistId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
	        return;
	    }
	    // is block too big or too small. NB: if maxBlock is 0, there are no songs in playlist, but the block should be shown
	    int maxBlock = (int) Math.ceil(totalSongs * 1.0 / songsPerBlock);
	    if (maxBlock != 0 && (maxBlock < currentBlock || currentBlock <= 0)) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The requested block does not exist.");
	        return;
	    }
	    int offset = (currentBlock - 1) * songsPerBlock;
	    try {
	        playlistSongsWithAlbum = songDetailsDAO.findSongsWithAlbumByPlaylistId(playlistId, offset, songsPerBlock);
			userSongs = songDAO.findAllSongsByUserIdNotBelongingToPlaylist(userId, playlistId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
	        return;
	    }
	    
	    Gson gson = new GsonBuilder()
	    		.setLenient()
                .create();
	
		 // Convert objects to JSON strings
	    String playlistSongsWithAlbumSerialized = gson.toJson(playlistSongsWithAlbum.entrySet()
	            .stream()
	            .map(e -> List.of(e.getKey(), e.getValue())) //List because of order
	            .collect(Collectors.toList()));
	    String userSongsSerialized = gson.toJson(userSongs);
	
		 // Set the response content type to JSON
		 response.setContentType("application/json");
	
		 // Write the response JSON string to the response output stream
		 response.getWriter().write(
				 "{\"playlistSongsWithAlbum\":" + playlistSongsWithAlbumSerialized + "," +
				 "\"userSongs\":" + userSongsSerialized + "," +
				 "\"currentBlock\":" + currentBlock + "," +
				 "\"maxBlock\":" + maxBlock + "," +
				 "\"songsPerBlock\":" + songsPerBlock + "}"
				 );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e){
				
			}
		}
	}
}
