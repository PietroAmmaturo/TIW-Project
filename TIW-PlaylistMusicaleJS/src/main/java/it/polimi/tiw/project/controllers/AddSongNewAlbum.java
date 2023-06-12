package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Year;

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

import it.polimi.tiw.project.DAO.AlbumDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.User;

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
		
		Year currentYear = Year.now();
		
		String songTitle = null;
		Part audioFile = null;
		String albumTitle = null;
		String albumArtist = null;
		Integer albumYear = null;
		String songGenre = null;
		Part albumCover = null;
		boolean valid = true;
		
		songTitle = request.getParameter("song_title");
		audioFile = request.getPart("audioFile");
		songGenre = request.getParameter("song_genre");
		albumTitle = request.getParameter("album_title");
		albumArtist = request.getParameter("album_artist");
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
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in contacting the db");
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
				String imageFileExtension = FileHandler.getFileExtension(albumCover);
				String imageFileName = albumTitle + "." + imageFileExtension;
				FileHandler.saveFile(getServletContext(), albumCover,  userId.toString(), imageFileName);
				albumDao.addAlbum(albumTitle, URLEncoder.encode(imageFileName, StandardCharsets.UTF_8), albumArtist, (int)albumYear, (int)userId);
			}else {
			//TODO titolo dell'album già in uso per l'utente + return
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album name already in use");
				return;
			}
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in adding the album");
			return;
		}
			
		try {
			idAlbum = albumDao.getAlbumIdByTitleAndUser(albumTitle, userId);
			//il nome del file è albumTitle_songTitle.extension
			String audioFileExtension = FileHandler.getFileExtension(audioFile);
			String audioFileName = albumTitle + "_" + songTitle + "." + audioFileExtension;
			FileHandler.saveFile(getServletContext(), audioFile,  userId.toString(), audioFileName);
			songDao.addSong(songTitle, songGenre, URLEncoder.encode(audioFileName, StandardCharsets.UTF_8), idAlbum);
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