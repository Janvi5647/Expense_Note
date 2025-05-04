package Project;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ExpenseNote {
    static String mobile;
    static int id;
    static String userName;
    static Scanner sc = new Scanner(System.in);
    static Income income;
    static Expense expense;
    static RecycleBin deletedData;
    static Connection con = DatabaseConnection.getConnection();
    static double total = 0;
    static BufferedWriter bw;
    static HashMap<String, String> userHashMap;

    public ExpenseNote() {
    }

    

    public  static void expenseNote() throws Exception {
        
        deletedData = new RecycleBin();
        userHashMap = new HashMap<>();
        String query = "select * from single_user";
        PreparedStatement pstmt = con.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            userHashMap.put(rs.getString("mobile_no"), rs.getString("user_name"));
        }

        // System.out.println("\t\t\t ~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~");
        // System.out.println("\t\t\t\t   EXPENSE TRACKER ");
        // System.out.println("\t\t\t ~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~");
        entry();
    }

    public static void login() throws Exception {
        bw = new BufferedWriter(new FileWriter("Log.txt"));
        bw.write("----------------------------------------------------------------------");
        bw.newLine();
        bw.write("Date           Detail      Mode     Income      Expense     Total");
        bw.newLine();
        bw.write("----------------------------------------------------------------------");
        bw.newLine();
        bw.flush();
        boolean isValidMobile = false;
        while (!isValidMobile) {

            System.out.print("\t\t\t Enter your mobile no.: ");
            mobile = sc.next();
            System.out.println();
            if (userHashMap.containsKey(mobile)) {
                isValidMobile = true;
                userName = userHashMap.get(mobile);
                System.out.println("\033[1m\033[32m\t\t\t\t Welcome " + userName + " \033[0m"); // [1m - bold, [32m -
                // green color, [0m -
                // bold
                System.out.println();
                String sql = "select user_id,user_name from single_user where mobile_no=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, mobile);
                ResultSet rs1 = pst.executeQuery();
                while (rs1.next()) {
                    id = (rs1.getInt("user_id"));
                    userName = (rs1.getString("user_name"));
                }
                // For log.txt (Recent data log)
                String sqllog = "select 'income' as table_name, income_id, date, detail, mode, amount, timestamp from income where user_id=? union all select 'expense' as table_name, expense_id, date, detail, mode, amount, timestamp from expense where user_id=? order by timestamp";

                PreparedStatement pstlog = con.prepareStatement(sqllog);
                pstlog.setInt(1, id);
                pstlog.setInt(2, id);

                ResultSet rslog = pstlog.executeQuery();

                int dateColumnIndex = rslog.findColumn("date");
                int detailColumnIndex = rslog.findColumn("detail");
                int modeColumnIndex = rslog.findColumn("mode");
                int amountColumnIndex = rslog.findColumn("amount");
                int tableNameColumnIndex = rslog.findColumn("table_name");

                while (rslog.next()) {
                    String tableName = rslog.getString(tableNameColumnIndex);
                    Date date = rslog.getDate(dateColumnIndex);
                    String detail = rslog.getString(detailColumnIndex);
                    String mode = rslog.getString(modeColumnIndex);
                    BigDecimal amount = rslog.getBigDecimal(amountColumnIndex);

                    if (tableName.equals("income")) {
                        ExpenseNote.total += amount.doubleValue();
                        bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                                date, detail, mode, amount, "", total));
                        bw.flush();
                    } else {
                        ExpenseNote.total -= amount.doubleValue();
                        bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                                date, detail, mode, "", amount, total));
                        bw.flush();
                    }
                }
                String time = "select current_timestamp";
                PreparedStatement ps = con.prepareStatement(time);
                ResultSet rs = ps.executeQuery();
                java.sql.Timestamp timeStamp = null;
                while (rs.next()) {
                    timeStamp = rs.getTimestamp("current_timestamp");
                }

                String userLogin = "update single_user set last_login=? where user_id=?";
                PreparedStatement pstt = con.prepareStatement(userLogin);
                pstt.setTimestamp(1, timeStamp);
                pstt.setInt(2, id);
                pstt.execute();
                Homepage();

            } else {
                System.out.println("\033[31m\t\t\t\t Logged in failed !!!! \033[0m");
                System.out.println();
                System.out.println("\t\t\t\t Invalid mobile no.");
                System.out.println("\t\t\t\t Please try again.");
                System.out.println();
                Thread.sleep(2000);
                entry();
            }
        }
    }

    public static void SignUp() throws Exception {
        boolean b = true;
        boolean mob = true;

        while (mob) {
            System.out.print("\t\t\t Enter mobile no : ");
            mobile = sc.next();
            if (userHashMap.containsKey(mobile)) {
                System.out.println("\t\t\t User Already Exists !!!!");
                entry();
                mob = false;
            }

            if (checkMobile(mobile)) {
                do {

                    int generatedOTP = generateOtp();
                    System.out.println("\t\t\t Generated OTP : " + generatedOTP);
                    System.out.print("\t\t\t Enter otp : ");
                    int otp = sc.nextInt();
                    sc.nextLine();
                    if (otp == generatedOTP) {
                        System.out.print("\t\t\t Enter Name : ");
                        String name = sc.nextLine();
                        String sql1 = "{call insertIntoSingleUser(?,?)}";
                        CallableStatement cst1 = con.prepareCall(sql1);
                        cst1.setString(1, name);
                        cst1.setString(2, mobile);
                        cst1.executeUpdate();
                        userHashMap.put(mobile, name);
                    } else {
                        System.out.println("\t\t\t Enter valid OTP");
                    }

                    mob = false;
                } while (!b);
            } else {
                System.out.println("\t\t\t Enter valid mobile number");
            }
        }
        System.out.println();
        System.out.println("\033[1m\033[32m\t\t\t\t SignUp Successful!!!!! \033[0m");
        System.out.println();
        System.out.println("\t\t\tLogIn to Proceed::");
        Thread.sleep(1500);
        System.out.println();
        entry();
    }

    public static void Homepage() throws Exception {
        income = new Income();
        expense = new Expense();
        boolean b = true;
        while (b) {
            System.out.println("\t\t\tPress [1] for Income");
            System.out.println("\t\t\tPress [2] for Expense");
            System.out.println("\t\t\tPress [3] to Show passbook");
            System.out.println("\t\t\tPress [4] for Recycle Bin");
            System.out.println("\t\t\tPress [5] to Update Mobile Number");
            System.out.println("\t\t\tPress [6] to take Backup");
            System.out.println("\t\t\tPress [7] to Restore Backup");
            System.out.println("\t\t\tPress [8] to get PieChart in Excel File");
            System.out.println("\t\t\tPress [9] to LogOut");
            System.out.print("\t\t\t Enter your choice: ");
            int ch = sc.nextInt();
            System.out.println();
            switch (ch) {
                case 1 -> {
                    // income
                    boolean b1 = true;
                    while (b1) {
                        System.out.println("\t\t\t 1. Add");
                        System.out.println("\t\t\t 2. Update");
                        System.out.println("\t\t\t 3. Delete");
                        System.out.println("\t\t\t 4. view");
                        System.out.println("\t\t\t 5. Exit");
                        System.out.print("\t\t\t Enter your choice: ");
                        int ch1 = sc.nextInt();
                        System.out.println();
                        switch (ch1) {
                            case 1 -> income.add();
                            case 2 -> income.update();
                            case 3 -> income.delete();
                            case 4 -> {
                                boolean b3 = true;
                                while (b3) {
                                    System.out.println("\t\t\t 1. View Today's Report");
                                    System.out.println("\t\t\t 2. View Monthly Report");
                                    System.out.println("\t\t\t 3. View Yearly Report");
                                    System.out.println("\t\t\t 4. Exit");
                                    System.out.print("\t\t\t Enter your choice: ");
                                    int ch3 = sc.nextInt();
                                    System.out.println();
                                    switch (ch3) {
                                        case 1 -> income.viewTodayReport();
                                        case 2 -> income.viewMonthlyReport();
                                        case 3 -> income.viewYearlyReport();
                                        case 4 -> b3 = false;
                                        default -> System.out.println("\t\t\t Invalid choice");
                                    }
                                }
                            }
                            case 5 -> b1 = false;
                            default -> System.out.println("\t\t\t Invalid choice");
                        }
                    }
                }
                case 2 -> {
                    // expense
                    boolean b4 = true;
                    while (b4) {
                        System.out.println("\t\t\t 1. Add");
                        System.out.println("\t\t\t 2. update");
                        System.out.println("\t\t\t 3. Delete");
                        System.out.println("\t\t\t 4. view");
                        System.out.println("\t\t\t 5. Exit");
                        System.out.print("\t\t\t Enter your choice: ");
                        int ch4 = sc.nextInt();
                        System.out.println();
                        switch (ch4) {
                            case 1 -> expense.add();
                            case 2 -> expense.update();
                            case 3 -> expense.delete();
                            case 4 -> {
                                boolean b3 = true;
                                while (b3) {
                                    System.out.println("\t\t\t 1. view today's report");
                                    System.out.println("\t\t\t 2. view monthly report");
                                    System.out.println("\t\t\t 3. view yearly report");
                                    System.out.println("\t\t\t 4. Exit");
                                    System.out.print("\t\t\t Enter your choice: ");
                                    int ch3 = sc.nextInt();
                                    System.out.println();
                                    switch (ch3) {
                                        case 1 -> expense.viewTodayReport();
                                        case 2 -> expense.viewMonthlyReport();
                                        case 3 -> expense.viewYearlyReport();
                                        case 4 -> b3 = false;
                                        default -> System.out.println("\t\t\t Invalid choice");
                                    }
                                }
                            }
                            case 5 -> b4 = false;
                        }
                    }
                }
                case 3 -> // passbook
                    passbook();

                case 4 -> {
                    boolean b_rb = true;
                    while (b_rb) {
                        System.out.println("\t\t\t 1. Display deleted data");
                        System.out.println("\t\t\t 2. Delete permanently");
                        System.out.println("\t\t\t 3. Retrive deleted data");
                        System.out.println("\t\t\t 4. Exit");
                        System.out.print("\t\t\t Enter your choice: ");
                        int ch_rb = sc.nextInt();
                        switch (ch_rb) {
                            case 1 -> deletedData.display();
                            case 2 -> deletedData.emptyRecycleBin();
                            case 3 -> deletedData.retrive();
                            case 4 -> b_rb = false;
                            default -> System.out.println("\t\t\t Invalid choice");
                        }
                    }
                }
                case 5 -> updateMobile();
                case 6 -> {
                    Backup backup = new Backup();
                    backup.Backupp();
                    
                }
                case 7 -> {
                    RestoreBackup restore = new RestoreBackup();
                    restore.Restore();
                }
                case 8 ->{
                    Expense.pieData();
                }
                case 9 -> {
                    System.out.println("\033[1m\033[32m\t\t\t\t Have A Nice Day " + userName + " \033[0m");
                    b = false;
                }
            }
        }
    }

    
    public static void updateMobile() {
        System.out.print("\t\t\tEnter New Number: ");
        String newMobile = sc.next();
        if (checkMobile(newMobile)) {

            try {
                String updateMobile = "update single_user set mobile_no=? where user_id=?";
                PreparedStatement pst = con.prepareStatement(updateMobile);
                pst.setString(1, newMobile);
                pst.setInt(2, ExpenseNote.id);
                pst.execute();
                boolean b = true;

                do {

                    int generatedOTP = generateOtp();
                    System.out.println("\t\t\t Generated OTP : " + generatedOTP);
                    System.out.print("\t\t\t Enter otp : ");
                    int otp = sc.nextInt();
                    sc.nextLine();
                    if (otp == generatedOTP) {
                        userHashMap.remove(mobile);
                        userHashMap.put(newMobile, userName); // Update userHashMap
                        System.out.println("\t\t\tMobile Number Updated.");
                    } else {
                        System.out.println("\t\t\t Enter valid OTP");
                    }

                } while (b);
            } catch (SQLException e) {
                if (e.getSQLState().equals("45000")) {
                    System.out.println("Duplicate mobile number found");
                } else {
                    System.out.println(e);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    public static void entry() throws Exception {
        
        System.out.println("\t\t\t ~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~");
        System.out.println("\t\t\t\t   EXPENSE TRACKER ");
        System.out.println("\t\t\t ~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~-~~~");
        boolean entry = true;
        while (entry) {
            System.out.println("\t\t\t 1. LogIn");
            System.out.println("\t\t\t 2. SignUp");
            System.out.println("\t\t\t 3. Exit");
            System.out.print("\t\t\t Enter your choice: ");
            int choice = sc.nextInt();
            System.out.println();
            switch (choice) {
                case 1 -> login();
                case 2 -> SignUp();
                case 3 ->
                    System.exit(0);
                default -> System.out.println("\t\t\t Enter valid choice");
            }
        }

    }

    public static int generateOtp()// generate otp during signUp
    {
        int otp = (int) (Math.random() * 1000000);
        return otp;
    }

    public static boolean checkMobile(String mobile) // check validation of mobile number
    {
        if ((mobile.length() == 10)
                && ((mobile.startsWith("9")) || (mobile.startsWith("8")) || (mobile.startsWith("7")))) {
            for (char ch : mobile.toCharArray()) {
                if (!Character.isDigit(ch)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static void passbook() throws Exception
    {
        System.out.println("\u001B[34;1m\t\t\t P A S S B O O K \u001B[0m");
        System.out.println("USER ID : " + id);
        System.out.println("NAME : " + userName);
        String sqllog = "select 'income' as table_name, income_id, date, detail, mode, amount, timestamp from income where user_id=? union all select 'expense' as table_name, expense_id, date, detail, mode, amount, timestamp from expense where user_id=? order by timestamp";

        PreparedStatement pstlog = con.prepareStatement(sqllog);
        pstlog.setInt(1, id);
        pstlog.setInt(2, id);

        ResultSet rslog = pstlog.executeQuery();

        int dateColumnIndex = rslog.findColumn("date");
        int detailColumnIndex = rslog.findColumn("detail");
        int modeColumnIndex = rslog.findColumn("mode");
        int amountColumnIndex = rslog.findColumn("amount");
        int tableNameColumnIndex = rslog.findColumn("table_name");

        System.out.println("----------------------------------------------------------------------");
        System.out.println(String.format("%-14s %-11s %-8s %-11s %-11s",
                "DATE", "DETAIL", "MODE", "INCOME", "EXPENSE"));
        System.out.println("----------------------------------------------------------------------");

        BigDecimal total = BigDecimal.ZERO;

        while (rslog.next()) {
            String tableName = rslog.getString(tableNameColumnIndex);
            Date date = rslog.getDate(dateColumnIndex);
            String detail = rslog.getString(detailColumnIndex);
            String mode = rslog.getString(modeColumnIndex);
            BigDecimal amount = rslog.getBigDecimal(amountColumnIndex);

            if (tableName.equals("income")) {
                total = total.add(amount);
                System.out.println(String.format("%-14s %-11s %-8s %-11s %-11s",
                        date, detail, mode, amount, ""));
            } else {
                total = total.subtract(amount);
                System.out.println(String.format("%-14s %-11s %-8s %-11s %-11s",
                        date, detail, mode, "", amount));
            }
        }

        System.out.println("----------------------------------------------------------------------");
        System.out.println("TOTAL : " + total);
        System.out.println();
    }

   
}
