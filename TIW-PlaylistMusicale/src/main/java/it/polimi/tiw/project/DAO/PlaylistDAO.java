package it.polimi.tiw.project.DAO;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Playlist;

public class PlaylistDAO {
	
	private Connection connection;

	public PlaylistDAO() {
		// TODO Auto-generated constructor stub
	}

	public PlaylistDAO(Connection con) {
		this.connection = con;
	}
	
	public Playlist findPlaylistById(int playlistId) throws SQLException {
	    Playlist playlist = null;
	    String query = "SELECT * FROM Playlist WHERE id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, playlistId);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                playlist = new Playlist();
	                playlist.setId(result.getInt("id"));
	                playlist.setTitle(result.getString("title"));
	                playlist.setPublicationDate(result.getTimestamp("publication_date").toLocalDateTime());
	                playlist.setDescription(result.getString("description"));
	                playlist.setUserId(result.getInt("user_id"));
	            }
	        }
	    }
	    return playlist;
	}
	
	public boolean doesPlaylistBelongToUser(int playlistId, int userId) throws SQLException {
	    try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Playlist WHERE id=? AND user_id=?")) {
	        statement.setInt(1, playlistId);
	        statement.setInt(2, userId);
	        try (ResultSet result = statement.executeQuery()) {
	            result.next();
	            int count = result.getInt(1);
	            return count > 0;
	        }
	    }
	}
	
	public List<Playlist> findPlaylistsByUserId(int userId) throws SQLException{
		String query = "SELECT * FROM Playlist WHERE user_id = ?";
		List<Playlist> playlistList = new ArrayList<>();
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();
			try {
				while (resultSet.next()) {
					Playlist playlist = new Playlist();
					int id = resultSet.getInt("id");
					String title = resultSet.getString("title");
					String description = resultSet.getString("description");
					int user_id = resultSet.getInt("user_id");
					LocalDateTime date = resultSet.getTimestamp("publication_date").toLocalDateTime();
					
					playlist.setPublicationDate(date);					
					playlist.setId(id);
					playlist.setTitle(title);
					playlist.setUserId(user_id);
               		playlistList.add(playlist);
				}
				return playlistList;
			}catch(SQLException e) {
	            e.printStackTrace();
	            throw e;
			}
		}
	}
	
}
