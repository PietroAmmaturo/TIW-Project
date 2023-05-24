package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import it.polimi.tiw.project.beans.User;

public class AlbumDAO{
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public boolean idInUse(int albumId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Album WHERE album_id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setInt(1, albumId);
		try(ResultSet resultSet = statement.executeQuery()){
			resultSet.next();
			int count = resultSet.getInt(1);
			return count >= 1;
		}catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void addAlbum(String title, String image, String interpreter, int publicationYear, int userId) throws SQLException {
		String query = "INSERT INTO Album (title, image, interpreter, publication_year, user_id) VALUES (?, ?, ?, ?, ?)";
		try(PreparedStatement statement = connection.prepareStatement(query)) {
			connection.setAutoCommit(false);
			statement.setString(1, title);
			statement.setString(2, image);
			statement.setString(3, interpreter);
			statement.setInt(4, publicationYear);
			statement.setInt(5, userId);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
                throw new SQLException("Failed to add album");
            }
		}catch(SQLException e){
			connection.rollback();
            throw e;
		}finally {
			connection.setAutoCommit(true);
		}
	}
	
	public boolean albumTitleInUseForUser(String title, int userId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Album WHERE title = ? AND user_id = ?";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1, title);
		statement.setInt(2, userId);
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