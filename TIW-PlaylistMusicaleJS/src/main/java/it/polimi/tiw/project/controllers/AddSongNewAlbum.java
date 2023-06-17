package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Year;

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
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utils.FileService;

@WebServlet("/AddSongNewAlbum")
@MultipartConfig
public class AddSongNewAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddSongNewAlbum() {
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
		
		String songTitle = null;
		Part audioFile = null;
		String albumTitle = null;
		String albumArtist = null;
		Integer albumYear = null;
		String songGenre = null;
		Part albumCover = null;
		
		songTitle = StringEscapeUtils.escapeJava(request.getParameter("song_title"));
		audioFile = request.getPart("audioFile");
		songGenre = StringEscapeUtils.escapeJava(request.getParameter("song_genre"));
		albumTitle = StringEscapeUtils.escapeJava(request.getParameter("album_title"));
		albumArtist = StringEscapeUtils.escapeJava(request.getParameter("album_artist"));
		albumCover = request.getPart("album_cover");
		albumYear = Integer.parseInt(request.getParameter("album_year"));
		
		boolean albumTitleInUse = true;
		boolean songTitleInUse = true;
		SongDAO songDao = new SongDAO(connection);
		AlbumDAO albumDao = new AlbumDAO(connection);
		int idAlbum = 0;
		
		try {
			songTitleInUse = songDao.titleAlreadyInUseForUser(songTitle, userId);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in contacting the database");
			return;
		}finally {
			if(songTitleInUse) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Song title already in use");
				return;
			}
		}

		try {
			albumTitleInUse = albumDao.albumTitleInUseForUser(albumTitle, userId);
			if(!albumTitleInUse) {
				//il nome del file è albumTitle.extension
				String imageFileExtension = FileService.getFileExtension(albumCover);
				String imageFileName = albumTitle + "." + imageFileExtension;
				FileService.saveFile(getServletContext(), albumCover,  userId.toString(), imageFileName);
				albumDao.addAlbum(albumTitle, imageFileName, albumArtist, (int)albumYear, (int)userId);
			}else {
			//titolo dell'album già in uso per l'utente
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album title already in use");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in adding the album");
			return;
		}
			
		try {
			idAlbum = albumDao.getAlbumIdByTitleAndUser(albumTitle, userId);
			//il nome del file è albumTitle_songTitle.extension
			String audioFileExtension = FileService.getFileExtension(audioFile);
			String audioFileName = albumTitle + "_" + songTitle + "." + audioFileExtension;
			FileService.saveFile(getServletContext(), audioFile,  userId.toString(), audioFileName);
			songDao.addSong(songTitle, songGenre, audioFileName, idAlbum);
			return;
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