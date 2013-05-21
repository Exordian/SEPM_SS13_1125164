package sepm.dao.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import org.apache.log4j.Logger;
import sepm.exceptions.PersistenceException;

public class DatabaseConnection {
	private static Logger logger = Logger.getLogger(DatabaseConnection.class);

	private static Connection connection;
	private DatabaseConnection() {}
	
	public static Connection getConnection(String uri, String username, String password) throws PersistenceException {
		if(connection == null) {
			try {
				Class.forName("org.hsqldb.jdbc.JDBCDriver");
				connection = DriverManager.getConnection(uri, username, password);
				connection.setAutoCommit(false);
			} catch (ClassNotFoundException e) {
				logger.error("HSQLDB driver not found", e);
            } catch (SQLException e) {
				logger.error("could not open database connection", e);
                throw new PersistenceException("couldnt open database connection");
			}
        }
		return connection;
	}
	
	public static Connection getConnection() throws PersistenceException {
		return getConnection("jdbc:hsqldb:hsql://localhost/xdb", "sa", "");
	}

    public static void closeConnection() {
        connection = null;
    }

}
