package Project;

import java.io.*;
import java.sql.*;

class RestoreBackup {
    public void Restore() {
        Connection conn = DatabaseConnection.getConnection();

        String backupFolder = "D:\\_CEA_23002171510013_DS_DBMS_JAVA-II\\backup\\" + ExpenseNote.userName;

        File folder = new File(backupFolder);
        File[] files = folder.listFiles();

        for (File file : files) {

            String tableName = file.getName().substring(0, file.getName().length() - 4);
            if (tableName.equals("expense") || tableName.equals("income")) {
                System.out.println("Restoring table: " + tableName);

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    String[] headers = null;

                    if ((line = br.readLine()) != null) {
                        headers = line.split(",");
                    }

                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        insertRowIntoTable(conn, tableName, headers, values);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file for table " + tableName + ": " + e.getMessage());
                } catch (SQLException e) {
                    System.err.println("Error restoring table " + tableName + ": " + e.getMessage());
                }
                System.out.println("\t\t\tRestored Data into "+tableName);
            }
        }
    }

    
    private static void insertRowIntoTable(Connection conn, String tableName, String[] headers, String[] values)
            throws SQLException {
        StringBuilder insertQuery = new StringBuilder("INSERT IGNORE INTO " + tableName + " (");

        for (int i = 0; i < headers.length; i++) {
            insertQuery.append(headers[i]);
            if (i < headers.length - 1) {
                insertQuery.append(", ");
            }
        }

        insertQuery.append(") VALUES (");

        for (int i = 0; i < values.length; i++) {
            insertQuery.append("? ");
            if (i < values.length - 1) {
                insertQuery.append(", ");
            }
        }

        insertQuery.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery.toString())) {
            for (int i = 0; i < values.length; i++) {
                pstmt.setString(i + 1, values[i]);
            }
            pstmt.executeUpdate();
        }
        
    }
}