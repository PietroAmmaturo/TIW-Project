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
	
	public void addUser(String username, String password) throws SQLException {
		String sql = "INSERT INTO User (nickname, password) VALUES (?, ?)";
		try(PreparedStatement statement = connection.prepareStatement(sql)) {
			connection.setAutoCommit(false);
			statement.setString(1, username);
			statement.setString(2, password);
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
	        statement.setString(1, username);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                user = new User();
	                user.setId(result.getInt("id"));
	                user.setUsername(result.getString("nickname"));
	                user.setPassword(result.getString("password"));
	            }
	            else {
	            	//se non c'è nessun utente con quel username ritorna null
	            	return null;
	            }
	        }
	    }
	    return user;
	}
	
	public User findUserByUsernameAndPassword(String username, String password) throws SQLException {
	    User user = null;
	    String query = "SELECT * FROM User WHERE nickname = ? AND password = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(1, username);
	        statement.setString(2, password);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                user = new User();
	                user.setId(result.getInt("id"));
	                user.setUsername(result.getString("nickname"));
	                user.setPassword(result.getString("password"));
	            }
	            else {
	            	//se non c'è nessun utente con quel username ritorna null
	            	return null;
	            }
	        }
	    }
	    return user;
	}
	
	/**
	 *  Return true if the username is already used, false otherwise
	 * @param username that needs to be checked
	 * @return
	 */
	public boolean usernameAlreadyInUse(String username) {
		String query = "SELECT COUNT(*) FROM User WHERE nickname = ?";
		try(PreparedStatement statement = connection.prepareStatement(query)){
			statement.setString(1, username);
			try(ResultSet resultSet = statement.executeQuery()){
				resultSet.next();
				int count = resultSet.getInt(1);
				return count > 1;
			}catch(SQLException e) {
				e.printStackTrace();
				return false;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}