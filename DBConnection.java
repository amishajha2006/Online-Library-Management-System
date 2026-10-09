
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";

    public static Connection getConnection() {
        String password = System.getenv("LIBRARY_DB_PASSWORD");

        if (password == null || password.isEmpty()) {
            System.out.println("Database password is not configured.");
            return null;
        }

        try {
            return DriverManager.getConnection(URL, USER, password);
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }
}