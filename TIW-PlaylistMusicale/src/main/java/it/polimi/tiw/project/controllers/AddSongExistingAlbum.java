package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.net.URLEncoder;
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

import it.polimi.tiw.project.DAO.AlbumDAO;
import it.polimi.tiw.project.DAO.SongDAO;
import it.polimi.tiw.project.beans.User;

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
		boolean valid = true;
		boolean showPopup = false;
		String errorMessage = "null";
		
		
		try {
			audioFile = request.getPart("audioFile");
			songTitle = request.getParameter("song_title");
			songGenre = request.getParameter("song_genre");
			albumId = Integer.parseInt(request.getParameter("albumId"));
			if(songTitle.isBlank() || songTitle.isEmpty() || audioFile.equals(null)) {
				valid = false;
				showPopup = true;
				errorMessage = "Missing or incorrect parameters";
			}
		}catch(NullPointerException e) {
			valid = false;
			showPopup = true;
			errorMessage = "Missing or incorrect parameters";
		}
		
		if(!valid) {
		    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or incorrect parameters");
			return;
		}
		
		if(valid) {
		boolean songTitleInUse = true;
		boolean albumIdValid = false;
		SongDAO songDao = new SongDAO(connection);
		AlbumDAO albumDao = new AlbumDAO(connection);
		try {
			//TODO aggiungere filtro per assicurarsi sia intero valido
			albumIdValid = albumDao.idInUse(albumId);
			if(albumIdValid)
				songTitleInUse = songDao.titleInAlbumAlreadyInUse(songTitle, albumId);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in contacting the db");
			return;
		}
		
		try {
			if(albumIdValid) {
				if(!songTitleInUse) {
					//TODO il nome del file dovebbe essere albumTitle-songTitle.extension, adesso è albumId-songTitle.extension
					String audioFileExtension = FileHandler.getFileExtension(audioFile);
					//TODO controllare che l'estensione non sia null
					String audioFileName = URLEncoder.encode(albumId + "_" + songTitle + "." + audioFileExtension, "UTF-8");
					FileHandler.saveFile(getServletContext(), audioFile,  userId.toString(), audioFileName);
					songDao.addSong(songTitle, songGenre, audioFileName, albumId);
			        String path = getServletContext().getContextPath() + "/GoToHome";
					response.sendRedirect(path);
				}else {
					//TODO titolo in uso nell'album
					showPopup = true;
					errorMessage = "Title already in use in the album";
				}
			}else {
				//TODO schermata che qualcuno ha manomesso l'id inviato
				showPopup = true;
				errorMessage = "You shouldn't see this...";
			}
		}catch (IOException | SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Error in adding the song");
			return;
		}
		}
		
		/*request.setAttribute("showPopup", showPopup);
	    request.setAttribute("inputValue", errorMessage);
	    RequestDispatcher dispatcher = request.getRequestDispatcher("src/main/webapp/WEB-INF/Home.html");
	    dispatcher.forward(request, response);*/
		
	}
	
	}
