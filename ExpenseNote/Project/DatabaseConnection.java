package Project;

import java.sql.*;

public class DatabaseConnection {
    static Connection con;

    public static Connection getConnection() {
        try {
            String dburl = "jdbc:mysql://localhost:3306/expensenote";
            String dbuser = "root";
            String dbpass = "";
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);
            con = DriverManager.getConnection(dburl, dbuser, dbpass);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return con;
    }
}
