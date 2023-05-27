package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Song;

public class SongDAO {
	
	private Connection connection;

	public SongDAO() {
		// TODO Auto-generated constructor stub
	}

	public SongDAO(Connection con) {
		this.connection = con;
	}
	
	public Song findSongById(int songId) throws SQLException {
	    String query = "SELECT * FROM Song WHERE id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, songId);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                Song song = new Song();
	                song.setId(result.getInt("id"));
	                song.setTitle(result.getString("title"));
	                song.setAudio(result.getString("audio"));
	                song.setAlbumId(result.getInt("album_id"));
	                song.setGenre(result.getString("genre"));
	                return song;
	            } else {
	                return null;
	            }
	        }
	    }
	}
	
	public List<Song> findAllSongsByPlaylistId(int playlistId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    String sql = "SELECT s.* FROM Song s " +
	                 "INNER JOIN SongPlaylist sp ON s.id = sp.song_id " +
	                 "WHERE sp.playlist_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, playlistId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("id"));
	                song.setTitle(resultSet.getString("title"));
	                song.setAudio(resultSet.getString("audio"));
	                song.setAlbumId(resultSet.getInt("album_id"));
	                song.setGenre(resultSet.getString("genre"));
	                songs.add(song);
	            }
	        }
	    }
	    return songs;
	}

	public List<Song> findAllSongsByUserId(int userId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    String query = "SELECT * FROM Song INNER JOIN Album ON Song.album_id = Album.id WHERE Album.user_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, userId);
	        try (ResultSet result = statement.executeQuery()) {
	            while (result.next()) {
	                Song song = new Song();
	                song.setId(result.getInt("id"));
	                song.setTitle(result.getString("title"));
	                song.setAudio(result.getString("audio"));
	                song.setGenre(result.getString("genre"));
	                songs.add(song);
	            }
	        }
	    }
	    return songs;
	}
	
	public boolean doesSongBelongToUser(int songId, int userId) throws SQLException {
	    try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Song INNER JOIN Album ON Song.album_id = Album.id WHERE Song.id=? AND Album.user_id=?")) {
	        statement.setInt(1, songId);
	        statement.setInt(2, userId);
	        try (ResultSet result = statement.executeQuery()) {
	            result.next();
	            int count = result.getInt(1);
	            return count > 0;
	        }
	    }
	}
	
	public List<Song> findAllSongsByUserIdNotBelongingToPlaylist(int userId, int playlistId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    String sql = "SELECT s.* FROM Song s " +
	                 "INNER JOIN Album a ON s.album_id = a.id " +
	                 "WHERE a.user_id = ? AND s.id NOT IN " +
	                 "(SELECT song_id FROM SongPlaylist WHERE playlist_id = ?)";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, userId);
	        statement.setInt(2, playlistId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("id"));
	                song.setTitle(resultSet.getString("title"));
	                song.setAudio(resultSet.getString("audio"));
	                song.setAlbumId(resultSet.getInt("album_id"));
	                song.setGenre(resultSet.getString("genre"));
	                songs.add(song);
	            }
	        }
	    }
	    return songs;
	}
	
	public void addSong(String titleSong,String genre, String audio ,int albumId) throws SQLException {
		String sql = "INSERT INTO Song (title, genre, audio, album_id) VALUES (?, ?, ?, ?)";
		try(PreparedStatement statement = connection.prepareStatement(sql)) {
			connection.setAutoCommit(false);
			statement.setString(1, titleSong);
			statement.setString(2, genre);
			statement.setString(3, audio);
			statement.setInt(4, albumId);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
                throw new SQLException("Failed to add song");
            }
		}catch(SQLException e){
			connection.rollback();
            throw e;
		}finally {
			connection.setAutoCommit(true);
		}
	}
	
	public boolean titleInAlbumAlreadyInUse(String titleSong, int albumId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Song WHERE title = ? AND album_id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, titleSong);
		statement.setInt(2, albumId);
		try(ResultSet resultSet = statement.executeQuery()){
			resultSet.next();
			int count = resultSet.getInt(1);
			return count >= 1;
		}catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
