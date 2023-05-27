package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.polimi.tiw.project.beans.Album;
import it.polimi.tiw.project.beans.User;

public class AlbumDAO{
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public boolean idInUse(int albumId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Album WHERE id = ?";
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
	
	public List<Album> findAlbumsByUserId(int userId) throws SQLException{
		String query = "SELECT * FROM Album WHERE user_id = ?";
		List<Album> albumsList = new ArrayList<>();
		ResultSet resultSet;
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setInt(1, userId);
			try {
			resultSet = statement.executeQuery();
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			try {
				while (resultSet.next()) {
					Album album = new Album();
					int id = resultSet.getInt("id");
					String title = resultSet.getString("title");
					String image = resultSet.getString("image");
					String interpreter = resultSet.getString("interpreter");
					int publicationYear = resultSet.getInt("publication_year");
					int user_id = resultSet.getInt("user_id");
					album.setId(id);
					album.setTitle(title);
					album.setImage(image);
					album.setInterpreter(interpreter);
					album.setPublicationYear(publicationYear);
					album.setUserId(user_id);
               		albumsList.add(album);
				}
				return albumsList;
			}catch(SQLException e) {
	            e.printStackTrace();
	            throw e;
			}
		}
	}
	
	public boolean albumTitleInUseForUser(String title, int userId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Album WHERE title = ? AND user_id = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
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
	
	public int getAlbumIdByTitleAndUser(String title, int userId) throws SQLException{
		String query = "SELECT * FROM Album WHERE title = ? AND user_id = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, title);
			statement.setInt(2, userId);
			try(ResultSet resultSet = statement.executeQuery()){
				resultSet.next();
				return resultSet.getInt("id");
			}
		}
	}
}
