package Project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Expense implements IncomeExpense{
    static Connection con = DatabaseConnection.getConnection();
    Scanner sc = new Scanner(System.in);
    LocalDate date;
    String detail;
    String category;
    int categoryId;
    double amount;
    String mode;
    double oldAmount;
    Timestamp timeStamp;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public Expense() throws Exception {
    }
    
    public Expense(LocalDate date, String detail, String category, int categoryId, double amount, String mode,
            Timestamp timeStamp) {
        this.date = date;
        this.detail = detail;
        this.category = category;
        this.categoryId = categoryId;
        this.amount = amount;
        this.mode = mode;
        this.timeStamp = timeStamp;
    }

    @Override
    public void add() throws Exception // add expense
    {
        System.out.println("\t\t\t Do you want to add to \n\t\t\t 1)Current date\n\t\t\t 2)Any Previous date");
        System.out.print("\t\t\t ");
        int datechoice = sc.nextInt();

        switch (datechoice) {
            case 1:
                this.date = LocalDate.now();
                break;
            case 2:
                boolean e1 = true;
                /////////////
                while (e1) {
                    try {
                        System.out.print("\t\t\t Enter date : ");
                        this.date = LocalDate.parse(sc.next());
                        e1 = false;
                    } catch (Exception e) {
                        System.out.println("\t\t\t Invalid date");
                        System.out.println("\t\t\tTRY AGAIN!!!");
                    }
                }
                //////////////////
                break;
            default:
                System.out.println("\t\t\t Invalid choice");
                return;
        }

        System.out.print("\t\t\t Enter detail of expense : ");
        sc.nextLine();
        this.detail = sc.nextLine();
        System.out.print("\t\t\t Enter amount : ");
        this.amount = sc.nextDouble();
        sc.nextLine();
        this.categoryId = categoryy.setCategory();
        System.out.print("\t\t\t Enter mode (cash/online) : ");
        this.mode = sc.nextLine();

        String time = "select current_timestamp";
        PreparedStatement ps = con.prepareStatement(time);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.timeStamp = rs.getTimestamp("current_timestamp");
        }

        String sql1 = "{call addExpense(?,?,?,?,?,?,?)}";
        CallableStatement cst1 = con.prepareCall(sql1);
        cst1.setObject(1, date);
        cst1.setString(2, detail);
        cst1.setDouble(3, amount);
        cst1.setString(4, mode);
        cst1.setInt(5, ExpenseNote.id);
        cst1.setInt(6, categoryId);
        cst1.setTimestamp(7, timeStamp);
        int r = cst1.executeUpdate();
        System.out.println((r > 0) ? "\t\t\t\tExpense Added" : "\t\t\t\tFailed !!!");
        System.out.println();

        ExpenseNote.total -= amount;
        ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                date, detail, mode, "", amount, ExpenseNote.total));
        ExpenseNote.bw.flush();
    }
    @Override
    public void update() throws Exception // update expense
    {
        String category_name = null;

        String updateDate = "";
        boolean e2 = true;
        Date d = null;
        while (e2) {

            try {
                System.out.print("\t\t\t Enter date(format:'YYYY-MM-DD') where you want to update : ");
                updateDate = sc.next();
                sdf.parse(updateDate);
                d = java.sql.Date.valueOf(updateDate);
                e2 = false;
            } catch (Exception e) {
                System.out.println();
                System.out.println("\t\t\tDate Entered is Invalid.");
                System.out.println("\t\t\tTRY AGAIN");
            }
        }

        String sql = "{call selectExpenseByDate(?,?) }";
        CallableStatement cst = con.prepareCall(sql);
        cst.setInt(1, ExpenseNote.id);
        cst.setDate(2, d);

        ArrayList<Integer> a = new ArrayList<>();
        ResultSet rs = cst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            System.out.println(
                    "\t\t\t ID          Date          Details          Amount          Category          Mode");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            sc.nextLine();

            while (rs.next()) {
                int cidd = rs.getInt(5);
                String q = "Select * from category where cid=?;";
                PreparedStatement pst = con.prepareStatement(q);
                pst.setInt(1, cidd);
                ResultSet rs1 = pst.executeQuery();

                if (rs1.next()) {
                    category_name = rs1.getString("cname");
                } else {
                    System.out.println("No category found with id " + cidd);
                }
                int idd = rs.getInt(1);
                a.add(idd);

                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-15s %-10s%n",
                        idd, rs.getDate(3), rs.getString(2), rs.getDouble(4), category_name, rs.getString(6));
            }
            System.out.println(
                    "\t\t\t---------------------------------------------------------------------------------------------");
            boolean e3 = true;
            int updateId = 0;
            //////
            while (e3) {
                System.out.print("\t\t\t Enter id whose data you want to modify : ");
                updateId = sc.nextInt();
                if (a.contains(updateId)) {
                    e3 = false;
                } else {
                    System.out.println("Enter valid ID");
                }
            }
            ////////

            System.out.println();

            boolean update = true;
            while (update) {
                System.out.println("\t\t\t 1. update detail");
                System.out.println("\t\t\t 2. update amount");
                System.out.println("\t\t\t 3. update category");
                System.out.println("\t\t\t 4. Exit");
                System.out.print("\t\t\t Enter your choice: ");
                int ch2 = sc.nextInt();
                sc.nextLine();
                System.out.println();
                switch (ch2) {
                    case 1 -> {
                        System.out.print("\t\t\t Enter new detail : ");
                        String newDetail = sc.nextLine();
                        String sql1 = "{ call updateExpenseDetail(?,?)}";
                        CallableStatement cst1 = con.prepareCall(sql1);
                        cst1.setInt(1, updateId);
                        cst1.setString(2, newDetail);
                        int r1 = cst1.executeUpdate();
                        System.out.println((r1 > 0) ? "\t\t\t\tUpdation Successful" : "\t\t\t\tFailed !!!");
                        System.out.println();

                        String sqlbw = "select mode,amount from expense where user_id=? and expense_id=? ";
                        PreparedStatement pst = con.prepareStatement(sqlbw);
                        pst.setInt(1, ExpenseNote.id);
                        pst.setInt(2, updateId);
                        ResultSet rsbw = pst.executeQuery();
                        while (rsbw.next()) {
                            mode = rsbw.getString("mode");
                            amount = rsbw.getDouble("amount");
                        }

                        ExpenseNote.bw.write("(Update in detail : )");
                        ExpenseNote.bw.newLine();
                        ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                                d, newDetail, mode, "", amount, ExpenseNote.total));
                        ExpenseNote.bw.flush();
                    }
                    case 2 -> {
                        double diff;
                        String sqlamt = "select amount from expense where expense_id=? and date=?";
                        PreparedStatement pstamt = con.prepareStatement(sqlamt);
                        pstamt.setInt(1, updateId);
                        pstamt.setDate(2, d);
                        ResultSet rs1 = pstamt.executeQuery();
                        while (rs1.next()) {
                            oldAmount = rs1.getDouble("amount");
                        }

                        System.out.print("\t\t\t Enter new amount : ");
                        Double newAmount = sc.nextDouble();
                        String sql2 = "{call updateExpenseAmount(?,?)}";
                        CallableStatement cst2 = con.prepareCall(sql2);
                        cst2.setInt(1, updateId);
                        cst2.setDouble(2, newAmount);
                        int r2 = cst2.executeUpdate();
                        System.out.println((r2 > 0) ? "\t\t\t\tUpdation Successful" : "\t\t\t\tFailed !!!");
                        System.out.println();

                        if (oldAmount > newAmount) {
                            diff = oldAmount - newAmount;
                            ExpenseNote.total = ExpenseNote.total + oldAmount - diff;
                        }
                        if (newAmount > oldAmount) {
                            diff = newAmount - oldAmount;
                            ExpenseNote.total = ExpenseNote.total - diff;
                        }

                        String sqlbw1 = "select mode,detail from expense where user_id=? and expense_id=? ";
                        PreparedStatement pst1 = con.prepareStatement(sqlbw1);
                        pst1.setInt(1, ExpenseNote.id);
                        pst1.setInt(2, updateId);
                        ResultSet rsbw1 = pst1.executeQuery();
                        while (rsbw1.next()) {
                            mode = rsbw1.getString("mode");
                            detail = rsbw1.getString("detail");
                        }

                        ExpenseNote.bw.write("(Update in amount : )");
                        ExpenseNote.bw.newLine();
                        ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                                d, detail, mode, "", newAmount, ExpenseNote.total));
                        ExpenseNote.bw.flush();
                    }
                    case 3 -> {
                        int newCid = categoryy.setCategory();
                        String sql3 = "{call updateExpenseCategory(?,?)}";
                        CallableStatement cst3 = con.prepareCall(sql3);
                        cst3.setInt(1, updateId);
                        cst3.setInt(2, newCid);
                        int r3 = cst3.executeUpdate();
                        System.out.println((r3 > 0) ? "\t\t\t\tUpdation Successful" : "\t\t\t\tFailed !!!");
                        System.out.println();
                    }
                    case 4 -> update = false;
                    default -> System.out.println("\t\t\t Invalid choice");
                }
            }
        } else {
            System.out.println("No record found to update.");
        }
    }
    @Override
    public void delete() throws Exception // delete expense
    {
        String category_name = null;
        int cidd = 0;
        boolean e4 = true;
        String deleteDate = "";
        Date d = null;
        while (e4) {

            try {
                System.out.print("\t\t\t Enter date(format:'YYYY-MM-DD') where you want to delete : ");
                deleteDate = sc.next();
                d = java.sql.Date.valueOf(deleteDate);
                sdf.parse(deleteDate);
                e4 = false;
            } catch (Exception e) {
                System.out.println("\t\t\t Invalid date format");
            }

        }

        String sql = "{call selectExpenseByDate(?,?) }";
        CallableStatement cst = con.prepareCall(sql);
        cst.setInt(1, ExpenseNote.id);
        cst.setDate(2, d);
        ArrayList<Integer> a = new ArrayList<>();
        ResultSet rs = cst.executeQuery();
        int idd;
        if (rs.isBeforeFirst()) {
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            System.out.println(
                    "\t\t\t ID          Date          Details          Amount          Category          Mode");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            sc.nextLine();

            while (rs.next()) {
                idd = rs.getInt("expense_id");
                a.add(idd);

                cidd = rs.getInt(5);
                String q = "Select * from category where cid=?";
                PreparedStatement pst = con.prepareStatement(q);
                pst.setInt(1, cidd);
                ResultSet rs1 = pst.executeQuery();

                if (rs1.next()) {
                    category_name = rs1.getString("cname");
                } else {
                    System.out.println("No category found with id " + cidd);
                }

                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-15s %-10s%n",
                        rs.getInt(1), rs.getDate(3), rs.getString(2), rs.getDouble(4), category_name, rs.getString(6));
            }
            System.out.println(
                    "\t\t\t---------------------------------------------------------------------------------------------");
            ////////////
            boolean e5 = true;
            int deleteId = 0;
            while (e5) {
                System.out.print("\t\t\t Enter id whose data you want to delete : ");
                deleteId = sc.nextInt();
                System.out.println();
                if (a.contains(deleteId)) {
                    e5 = false;
                } else {
                    System.out.println("Invalid ID");
                    return;
                }
            } /////////////
            String sql2 = "select * from expense where expense_id=? and date=?";
            PreparedStatement pst = con.prepareStatement(sql2);
            pst.setInt(1, deleteId);
            pst.setDate(2, d);
            ResultSet rs2 = pst.executeQuery();
            while (rs2.next()) {
                amount = rs2.getDouble("amount");
            }

            ExpenseNote.total += amount;

            String sqlbw = "select mode,detail from expense where user_id=? and expense_id=? ";
            PreparedStatement pst1 = con.prepareStatement(sqlbw);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, deleteId);
            ResultSet rsbw = pst1.executeQuery();
            while (rsbw.next()) {
                mode = rsbw.getString("mode");
                detail = rsbw.getString("detail");

            }
            ExpenseNote.bw.write("Below expense is deleted : ");
            ExpenseNote.bw.newLine();

            ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                    d, detail, mode, "", amount, ExpenseNote.total));
            ExpenseNote.bw.flush();

            String time = "select current_timestamp";
            PreparedStatement pst_time = con.prepareStatement(time);
            ResultSet rs_time = pst_time.executeQuery();
            while (rs_time.next()) {
                this.timeStamp = rs_time.getTimestamp(1);
            }

            LocalDate d1 = d.toLocalDate();
            Expense e = new Expense(d1, detail, category_name, cidd, amount, mode, timeStamp);
            ExpenseNote.deletedData.insertDeletedData(e);

            String sql1 = "{call deleteExpense(?)}";
            CallableStatement cst1 = con.prepareCall(sql1);
            cst1.setInt(1, deleteId);
            int r = cst1.executeUpdate();
            System.out.println((r > 0) ? "\t\t\t Record Deleted" : "\t\t\t No Record Deleted");
            System.out.println();
        } else {
            System.out.println("\t\t\t No Record Found");
        }
    }

    @Override
    public void viewTodayReport() throws Exception {
        String category_name = null;
        LocalDate l_date = LocalDate.now();
        String todayDate = l_date.toString();
        String sql = "Select * from expense where date=? and user_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, todayDate);
        pst.setInt(2, ExpenseNote.id);
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t\t\t\t Today's Report:");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            System.out.println(
                    "\t\t\t ID          Date          Details          Amount          Category          Mode");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int cidd = rs.getInt(5);
                String q = "Select * from category where cid=?;";
                PreparedStatement pst1 = con.prepareStatement(q);
                pst1.setInt(1, cidd);
                ResultSet rs1 = pst1.executeQuery();

                if (rs1.next()) {
                    category_name = rs1.getString("cname");
                } else {
                    System.out.println("No category found with id " + cidd);
                }

                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-15s %-10s%n",
                        rs.getInt(1), rs.getDate(3), rs.getString(2), rs.getDouble(4), category_name, rs.getString(6));
            }
            System.out.println(
                    "\t\t\t---------------------------------------------------------------------------------------------");
            System.out.println();
        } else {
            System.out.println("\t\t\t\t No Record Found !!!");
            System.out.println();
        }
    }

    @Override
    public void viewMonthlyReport() throws Exception {
        String category_name = null;
        System.out.print("\t\t\t Enter month (1-12) : ");
        // sc.nextLine();
        String monthNo = sc.nextLine();
        System.out.print("\t\t\t Enter year : ");
        String year = sc.nextLine();

        String sql = "select * from expense where user_id=? and MONTH(date)=? and YEAR(date)=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, ExpenseNote.id);
        pst.setInt(2, Integer.parseInt(monthNo));
        pst.setInt(3, Integer.parseInt(year));
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t Monthly Report for " + monthNo + "/" + year + ":");
            System.out.println("\t\t\t Today's Report:");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            System.out.println(
                    "\t\t\t ID          Date          Details          Amount          Category          Mode");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int cidd = rs.getInt(5);
                String q = "Select * from category where cid=?;";
                PreparedStatement pst1 = con.prepareStatement(q);
                pst1.setInt(1, cidd);
                ResultSet rs1 = pst1.executeQuery();

                if (rs1.next()) {
                    category_name = rs1.getString("cname");
                } else {
                    System.out.println("No category found with id " + cidd);
                }

                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-15s %-10s%n",
                        rs.getInt(1), rs.getDate(3), rs.getString(2), rs.getDouble(4), category_name, rs.getString(6));
            }
            System.out.println(
                    "\t\t\t---------------------------------------------------------------------------------------------");
            System.out.println();
            String sql1 = "select sum(amount) as totalAmount from expense where user_id=? and MONTH(date)=? and YEAR(date)=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, Integer.parseInt(monthNo));
            pst1.setInt(3, Integer.parseInt(year));
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                double totalamount = rs1.getDouble("totalAmount");
                System.out.println("\t\t\t Total monthly expense : " + totalamount);
            }
            System.out.println();
        } else {
            System.out.println("\t\t\t\t No expense found for this month");
            System.out.println();
        }
    }
    @Override
    public void viewYearlyReport() throws Exception {
        String category_name = null;
        System.out.print("\t\t\t Enter year : ");
        String year = sc.nextLine();

        String sql = "select * from expense where user_id=? and YEAR(date)=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, ExpenseNote.id);
        pst.setInt(2, Integer.parseInt(year));
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t Yearly Report for " + year + ":");
            System.out.println("\t\t\t Today's Report:");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");
            System.out.println(
                    "\t\t\t ID          Date          Details          Amount          Category          Mode");
            System.out.println(
                    "\t\t\t --------------------------------------------------------------------------------------------");

            while (rs.next()) {
                int cidd = rs.getInt(5);
                String q = "Select * from category where cid=?;";
                PreparedStatement pst1 = con.prepareStatement(q);
                pst1.setInt(1, cidd);
                ResultSet rs1 = pst1.executeQuery();

                if (rs1.next()) {
                    category_name = rs1.getString("cname");
                } else {
                    System.out.println("No category found with id " + cidd);
                }

                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-15s %-10s%n",
                        rs.getInt(1), rs.getDate(3), rs.getString(2), rs.getDouble(4), category_name, rs.getString(6));
            }
            System.out.println(
                    "\t\t\t---------------------------------------------------------------------------------------------");
            System.out.println();

            String sql1 = "select sum(amount) as totalAmount from expense where user_id=? and YEAR(date)=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, Integer.parseInt(year));
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                double totalamount = rs1.getDouble("totalAmount");
                System.out.println("\t\t\t Total yearly expense : " + totalamount);

            }
            System.out.println();
        } else {
            System.out.println("\t\t\t\t No expense found for this year !!!");
            System.out.println();
        }
    }

    @Override
    public String toString() {
        return "Expense [date=" + date + ", detail=" + detail + ", category=" + category + ", categoryId=" + categoryId
                + ", amount=" + amount + ", mode=" + mode + ", timeStamp=" + timeStamp + "]";
    }

    public static void pieData() throws Exception
    {
        BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\_CEA_23002171510013_DS_DBMS_JAVA-II\\backup\\" + ExpenseNote.userName+ "\\piedata.xlsx"));
        bw.write("Category,Amount\n");
        String sql="SELECT c.cid AS cid, c.cname AS Category, SUM(e.amount) AS TotalAmount FROM expense e JOIN category c ON e.cid = c.cid WHERE user_id="+ExpenseNote.id+" GROUP BY c.cname";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while(rs.next())
        {
            bw.write(rs.getInt("cid")+","+rs.getString("Category") + "," + rs.getDouble("TotalAmount") +"\n");
        }
        bw.close();
    }


}
