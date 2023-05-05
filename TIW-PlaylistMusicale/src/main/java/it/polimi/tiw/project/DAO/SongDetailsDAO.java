package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.Song;

// class used for query operations that do not include
public class SongDetailsDAO {
	
	private Connection connection;

	public SongDetailsDAO() {
		// TODO Auto-generated constructor stub
	}

	public SongDetailsDAO(Connection con) {
		this.connection = con;
	}
	
	public int countSongsByPlaylistId(int playlistId) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM SongPlaylist WHERE playlist_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, playlistId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            if (resultSet.next()) {
	                return resultSet.getInt(1);
	            }
	        }
	    }
	    return 0;
	}
	
	public Map<Song, Album> findAllSongsWithAlbumByPlaylistId(int playlistId) throws SQLException {
	    Map<Song, Album> songAlbumMap = new HashMap<>();
	    String sql = "SELECT s.*, a.* FROM Song s " +
	                 "INNER JOIN SongPlaylist sp ON s.id = sp.song_id " +
	                 "INNER JOIN Album a ON s.album_id = a.id " +
	                 "WHERE sp.playlist_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, playlistId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("s.id"));
	                song.setTitle(resultSet.getString("s.title"));
	                song.setAudio(resultSet.getString("s.audio"));

	                Album album = new Album();
	                album.setId(resultSet.getInt("a.id"));
	                album.setTitle(resultSet.getString("a.title"));
	                album.setImage(resultSet.getString("a.image"));
	                album.setInterpreter(resultSet.getString("a.interpreter"));
	                album.setPublicationYear(resultSet.getInt("a.publication_year"));

	                songAlbumMap.put(song, album);
	            }
	        }
	    }
	    return songAlbumMap;
	}
	
	public Map<Song, Album> findSongWithAlbumById(int songId) throws SQLException {
	    Map<Song, Album> songAlbumMap = new HashMap<>();
	    String sql = "SELECT s.*, a.* FROM Song s " +
	                 "INNER JOIN Album a ON s.album_id = a.id " +
	                 "WHERE s.id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, songId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("s.id"));
	                song.setTitle(resultSet.getString("s.title"));
	                song.setAudio(resultSet.getString("s.audio"));

	                Album album = new Album();
	                album.setId(resultSet.getInt("a.id"));
	                album.setTitle(resultSet.getString("a.title"));
	                album.setImage(resultSet.getString("a.image"));
	                album.setInterpreter(resultSet.getString("a.interpreter"));
	                album.setPublicationYear(resultSet.getInt("a.publication_year"));

	                songAlbumMap.put(song, album);
	            }
	        }
	    }
	    return songAlbumMap;
	}
	
	public Map<Song, Album> findSongsWithAlbumByPlaylistId(int playlistId, int offset, int limit) throws SQLException {
	    Map<Song, Album> songAlbumMap = new HashMap<>();
	    String sql = "SELECT s.*, a.* FROM Song s " +
	                 "INNER JOIN SongPlaylist sp ON s.id = sp.song_id " +
	                 "INNER JOIN Album a ON s.album_id = a.id " +
	                 "WHERE sp.playlist_id = ? " +
	                 "ORDER BY a.publication_year DESC " +
	                 "LIMIT ? OFFSET ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, playlistId);
	        statement.setInt(2, limit);
	        statement.setInt(3, offset);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("s.id"));
	                song.setTitle(resultSet.getString("s.title"));
	                song.setAudio(resultSet.getString("s.audio"));
	
	                Album album = new Album();
	                album.setId(resultSet.getInt("a.id"));
	                album.setTitle(resultSet.getString("a.title"));
	                album.setImage(resultSet.getString("a.image"));
	                album.setInterpreter(resultSet.getString("a.interpreter"));
	                album.setPublicationYear(resultSet.getInt("a.publication_year"));
	
	                songAlbumMap.put(song, album);
	            }
	        }
	    }
	    return songAlbumMap;
	}
}
