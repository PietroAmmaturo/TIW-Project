package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistDAO {
	
	private Connection connection;

	public PlaylistDAO() {
		// TODO Auto-generated constructor stub
	}

	public PlaylistDAO(Connection con) {
		this.connection = con;
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
}
