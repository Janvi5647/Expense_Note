package Project;

import java.sql.*;
import java.util.*;

public class categoryy {
    static Connection con = DatabaseConnection.getConnection();
    static Scanner sc = new Scanner(System.in);

    public static int setCategory() throws Exception//setting category for expense by user
     {
        String sql = "{call showCategory()}";
        CallableStatement cst = con.prepareCall(sql);
        ResultSet rs = cst.executeQuery();
        System.out.println("\t\t\t -----------------------");
        System.out.println("\t\t\t ID         Category");
        System.out.println("\t\t\t -----------------------");
        while (rs.next()) {
            System.out.printf("\t\t\t %-10s %-14s%n",
                    rs.getInt(1), rs.getString(2));
        }
        System.out.println("\t\t\t -----------------------");

        System.out.print("\t\t\t Enter category id or press'0' to add new Category: ");
        int ch = sc.nextInt();
        sc.nextLine();
        if (ch == 0) {
            System.out.println("\t\t\t Enter new category name : ");
            String newCategory = sc.next();
            String q1 = "INSERT INTO category(cname) values('" + newCategory + "');";
            PreparedStatement pst = con.prepareStatement(q1);
            pst.executeUpdate();

            String sqlId = "select cid from category where cname='" + newCategory + "';";
            PreparedStatement pst1 = con.prepareStatement(sqlId);
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                return rs1.getInt(1);
            }
        } else {
            return ch;
        }
        return ch;
    }
}
