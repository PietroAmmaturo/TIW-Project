package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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

import it.polimi.tiw.project.DAO.PlaylistDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.DAO.SongDetailsDAO;
import it.polimi.tiw.project.beans.Song;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Playlist;

import org.thymeleaf.templateresolver.ITemplateResolver;
/**
 * Servlet implementation class GoToPlaylist
 */
@WebServlet("/GoToPlaylist")
public class GoToPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToPlaylist() {
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
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		HttpSession session = request.getSession(false);
		int userId = ((User) session.getAttribute("currentUser")).getId();
		int playlistId = Integer.parseInt(request.getParameter("playlistId"));
	    int songsPerPage = 5;
		int totalSongs;
	    int currentPage;
	    Playlist playlist;
	    // page validation
        try{
        	currentPage = Integer.parseInt(request.getParameter("playlistPage"));
        }catch (Exception e) {
        	currentPage = 1;
        }
		PlaylistDAO playlistDao = new PlaylistDAO(connection);
		try {
	        playlist = playlistDao.findPlaylistById(playlistId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
	        return;
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
	    // is page too big or too small. NB: if maxPage is 0, there are no songs in playlist, but the page should be shown
	    int maxPage = (int) Math.ceil(totalSongs * 1.0 / songsPerPage);
	    if (maxPage != 0 && (maxPage < currentPage || currentPage <= 0)) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The requested page does not exist.");
	        return;
	    }
	    int offset = (currentPage - 1) * songsPerPage;
	    try {
	        playlistSongsWithAlbum = songDetailsDAO.findSongsWithAlbumByPlaylistId(playlistId, offset, songsPerPage);
			userSongs = songDAO.findAllSongsByUserIdNotBelongingToPlaylist(userId, playlistId);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
	        return;
	    }
	    //request.setAttribute("playlist", playlist);
		String path = "/WEB-INF/Playlist.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playlistSongsWithAlbum", playlistSongsWithAlbum);
		ctx.setVariable("userSongs", userSongs);
		ctx.setVariable("maxPage", maxPage);
		ctx.setVariable("currentPage", currentPage);
		ctx.setVariable("playlistId", playlistId);
		ctx.setVariable("playlist", playlist);
		ctx.setVariable("error", session.getAttribute("error"));
		session.removeAttribute("error");
		templateEngine.process(path, ctx, response.getWriter());
		/*RequestDispatcher dispatcher = request.getRequestDispatcher("/playlist.html");
	    dispatcher.forward(request, response);*/
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
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