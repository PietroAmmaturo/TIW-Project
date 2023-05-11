package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class UserDAO{
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void addUser(int userID, String username, String password){
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
}