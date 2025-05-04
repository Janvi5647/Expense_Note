package Project;

import java.io.*;
import java.sql.*;

public class Backup {
    Connection con = DatabaseConnection.getConnection();

    public void Backupp() {

        String backupFolder = "D:\\_CEA_23002171510013_DS_DBMS_JAVA-II\\backup\\" + ExpenseNote.userName; // specify the backup folder

        // Createss the backup folder if it doesn't exist.
        File folder = new File(backupFolder);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        try {
            // Backup expense table
            String sql = "SELECT * FROM expense WHERE user_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, ExpenseNote.id);
            ResultSet rs = pst.executeQuery();
            backupTable(rs, backupFolder + "/expense.csv");

            // Backup category table
            sql = "SELECT * FROM category";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            backupTable(rs, backupFolder + "/category.csv");

            // Backup Incomr table
            sql = "SELECT * FROM income WHERE user_id=?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, ExpenseNote.id);
            rs = pst.executeQuery();
            backupTable(rs, backupFolder + "/income.csv");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("\t\t\tBackup Completed");
    }

    private  void backupTable(ResultSet rs, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

        
            for (int i = 1; i <= columnCount; i++) {
                bw.write(rsmd.getColumnName(i));
                if (i < columnCount) {
                    bw.write(",");
                }
            }
            bw.newLine();

    
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    bw.write(rs.getString(i));
                    if (i < columnCount) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
}

