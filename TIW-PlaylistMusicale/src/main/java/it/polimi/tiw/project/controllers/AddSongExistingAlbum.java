package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.project.DAO.AlbumDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utils.FileService;

@WebServlet("/AddSongExistingAlbum")
@MultipartConfig
public class AddSongExistingAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
	public AddSongExistingAlbum() {
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
		
		int albumId = 0;
		String songTitle = null;
		String songGenre = null;
		Part audioFile = null;
		
		audioFile = request.getPart("audioFile");
		songTitle = StringEscapeUtils.escapeJava(request.getParameter("song_title"));
		songGenre = StringEscapeUtils.escapeJava(request.getParameter("song_genre"));
		albumId = Integer.parseInt(request.getParameter("albumId"));
		
		boolean songTitleInUse = true;
		SongDAO songDao = new SongDAO(connection);
		AlbumDAO albumDao = new AlbumDAO(connection);
		
		try {
			songTitleInUse = songDao.titleAlreadyInUseForUser(songTitle, userId);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in contacting the db");
			return;
		}
		
		try {
			if(!songTitleInUse) {
			//il nome del file è albumTitle_songTitle.extension
			String audioFileExtension = FileService.getFileExtension(audioFile);
			String albumTitle = albumDao.getTitleById(albumId, userId);
			String audioFileName = albumTitle + "_" + songTitle + "." + audioFileExtension;
			FileService.saveFile(getServletContext(), audioFile,  userId.toString(), audioFileName);
			songDao.addSong(songTitle, songGenre, audioFileName, albumId);
			String path = getServletContext().getContextPath() + "/GoToHome";
			response.sendRedirect(path);
			        
			}else {
				//titolo in uso per l'utente
				session.setAttribute("error", "Song title already in use");
		       	String path = getServletContext().getContextPath() + "/GoToHome";
				response.sendRedirect(path);
				return;
			}
		}catch (IOException | SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in adding the song");
			return;
		}
		

		
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
