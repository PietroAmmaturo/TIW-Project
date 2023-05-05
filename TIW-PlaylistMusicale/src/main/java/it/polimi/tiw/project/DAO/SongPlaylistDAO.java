package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SongPlaylistDAO {
    private Connection connection;

    public SongPlaylistDAO(Connection connection) {
        this.connection = connection;
    }

    public void deleteSongFromPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "DELETE FROM SongPlaylist WHERE song_id = ? AND playlist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
            statement.executeUpdate();
        }
    }

    public void addSongToPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "INSERT INTO SongPlaylist (song_id, playlist_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
            statement.executeUpdate();
        }
    }
    
    public boolean doesSongBelongToPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SongPlaylist WHERE song_id = ? AND playlist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
	        try (ResultSet result = statement.executeQuery()) {
	            result.next();
	            int count = result.getInt(1);
	            return count > 0;
	        }
        }
    }







}