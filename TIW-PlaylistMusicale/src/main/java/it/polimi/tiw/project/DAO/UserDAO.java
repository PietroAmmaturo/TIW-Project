package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import it.polimi.tiw.project.beans.User;

public class UserDAO{
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void addUser(int userID, String username, String password) throws SQLException {
		String sql = "INSERT INTO User (id, nickname, password) VALUES (?, ?, ?)";
		try(PreparedStatement statement = connection.prepareStatement(sql)) {
			connection.setAutoCommit(false);
			statement.setInt(1, userID);
			statement.setString(2, username);
			statement.setString(3, password);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
                throw new SQLException("Failed to add user");
            }
		}catch(SQLException e){
			connection.rollback();
            throw e;
		}finally {
			connection.setAutoCommit(true);
		}
	}
	
	public User findUserByUsername(String username) throws SQLException {
	    User user = null;
	    String query = "SELECT * FROM User WHERE nickname = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(2, username);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                user = new User();
	                user.setId(result.getInt("id"));
	                user.setUsername(result.getString("nickname"));
	                user.setPassword(result.getString("password"));
	            }
	        }
	    }
	    return user;
	}
	
	public User findUserByUsernameAndPassword(String username, String password) throws SQLException {
	    User user = null;
	    String query = "SELECT * FROM User WHERE nickname = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(2, username);
	        statement.setString(3, password);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                user = new User();
	                user.setId(result.getInt("id"));
	                user.setUsername(result.getString("nickname"));
	                user.setPassword(result.getString("password"));
	            }
	        }
	    }
	    return user;
	}
}