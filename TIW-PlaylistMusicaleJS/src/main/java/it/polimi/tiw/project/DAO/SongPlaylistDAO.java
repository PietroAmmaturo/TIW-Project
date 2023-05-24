package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class SongPlaylistDAO {
    private Connection connection;

    public SongPlaylistDAO(Connection connection) {
        this.connection = connection;
    }

    public void removeSongFromPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "DELETE FROM SongPlaylist WHERE song_id = ? AND playlist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to delete song from playlist");
            }
        }
    }

    public void removeSongsFromPlaylist(Set<Integer> songIds, int playlistId) throws SQLException {
        String sql = "DELETE FROM SongPlaylist WHERE song_id = ? AND playlist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false); // start transaction
            for (int songId : songIds) {
                statement.setInt(1, songId);
                statement.setInt(2, playlistId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to delete song from playlist");
                }
            }
            connection.commit(); // commit transaction
        } catch (SQLException ex) {
            connection.rollback(); // rollback transaction
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void addSongToPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "INSERT INTO SongPlaylist (song_id, playlist_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to add song to playlist");
            }
        }
    }

    public void addSongsToPlaylist(Set<Integer> songIds, int playlistId) throws SQLException {
        String sql = "INSERT INTO SongPlaylist (song_id, playlist_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Integer songId : songIds) {
                statement.setInt(1, songId);
                statement.setInt(2, playlistId);
                statement.addBatch();
            }
            int[] affectedRows = statement.executeBatch();
            connection.commit();
            for (int rows : affectedRows) {
                if (rows == 0) {
                    throw new SQLException("Failed to add song to playlist");
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public boolean doesSongBelongToPlaylist(int songId, int playlistId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SongPlaylist WHERE song_id = ? AND playlist_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, songId);
            statement.setInt(2, playlistId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    int count = result.getInt(1);
                    return count > 0;
                } else {
                    throw new SQLException("Failed to check if song belongs to playlist, no rows returned by query");
                }
            }
        }
    }
}