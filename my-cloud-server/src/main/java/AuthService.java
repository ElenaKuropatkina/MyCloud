import java.sql.*;

public class AuthService {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:myDB.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ident(String login, String pass) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT password FROM usersHash WHERE login = '" + login + "'");
            int userHash = pass.hashCode();
            if (rs.next()) {
                int dbHash = rs.getInt(2);
                if (dbHash == userHash) {
                    connect();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

