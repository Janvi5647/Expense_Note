package Project;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Income implements IncomeExpense {

    static Connection con = DatabaseConnection.getConnection();
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Scanner sc = new Scanner(System.in);
    LocalDate date;
    String detail;
    double amount;
    String mode;
    double oldAmount;
    Timestamp timeStamp;

    public Income() {
    }


    public Income(LocalDate date, String detail, double amount, String mode, Timestamp timeStamp) throws Exception {
        this.date = date;
        this.detail = detail;
        this.amount = amount;
        this.mode = mode;
        this.timeStamp = timeStamp;
    }

    @Override
    public void add() throws Exception //Add income
    {

        System.out.println("\t\t\t Do you want to add to \n\t\t\t 1)Current date\n\t\t\t 2)Any Previous date");
        System.out.print("\t\t\t ");
        int datechoice = sc.nextInt();
        System.out.println();

        switch (datechoice) {
            case 1 -> this.date = LocalDate.now();

            case 2 -> {
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
            }
            default -> {
                System.out.println("\t\t\t Invalid choice");
                return;
            }
        }

        System.out.print("\t\t\t Enter detail of income : ");
        sc.nextLine();
        this.detail = sc.nextLine();
        System.out.print("\t\t\t Enter amount : ");
        this.amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("\t\t\t Enter mode (cash/online) : ");
        this.mode = sc.nextLine();

        String time = "select current_timestamp";
        PreparedStatement ps = con.prepareStatement(time);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.timeStamp = rs.getTimestamp("current_timestamp");
        }

        String sql = "{call addIncome(?,?,?,?,?,?)}";
        CallableStatement cst = con.prepareCall(sql);
        cst.setObject(1, date);
        cst.setString(2, detail);
        cst.setDouble(3, amount);
        cst.setString(4, mode);
        cst.setInt(5, ExpenseNote.id);
        cst.setTimestamp(6, timeStamp);
        int r = cst.executeUpdate();
        System.out.println();
        System.out.println((r > 0) ? "\t\t\t\tIncome Added" : "\t\t\t\tFailed !!!");
        System.out.println();

        ExpenseNote.total += amount;
        ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                date, detail, mode, amount, "", ExpenseNote.total));
        ExpenseNote.bw.flush();
    }

    @Override
    public void update() throws Exception //Update income
    {

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
        
        //
        String sql = "{call selectbyDate(?,?)}";
        CallableStatement cst = con.prepareCall(sql);
        cst.setDate(1, d);
        cst.setInt(2, ExpenseNote.id);
        ResultSet rs = cst.executeQuery();
        ArrayList<Integer> a=new ArrayList<>();
        int idd;
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println("\t\t\t ID          Date          Details          Amount          Mode");
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            while (rs.next()) {
                idd=rs.getInt(1);
                a.add(idd);
                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-20s%n",
                        idd, rs.getDate(2), rs.getString(3), rs.getDouble(4), rs.getString(5));
            }
            System.out.println("\t\t\t ------------------------------------------------------------------------");

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
                System.out.println("\t\t\t 3. Exit");
                System.out.print("\t\t\t Enter your choice: ");
                int ch2 = sc.nextInt();
                sc.nextLine();
                System.out.println();

                switch (ch2) {
                    case 1 -> {   //UPDATE INCOME DETAIL
                        System.out.print("\t\t\t Enter new detail : ");
                        String newDetail = sc.nextLine();
                        String sql1 = "{ call updateDetail(?,?)}";
                        CallableStatement cst1 = con.prepareCall(sql1);
                        cst1.setObject(1, updateId);
                        cst1.setString(2, newDetail);
                        int r1 = cst1.executeUpdate();
                        System.out.println((r1 > 0) ? "\t\t\t\tUpdation Successful" : "\t\t\t\tFailed !!!");
                        System.out.println();

                        String sqlbw = "select mode,amount from income where user_id=? and income_id=? ";
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
                                d, newDetail, mode, amount, "", ExpenseNote.total));
                        ExpenseNote.bw.flush();
                    }
                    case 2 -> { //UPDATE INCOME AMOUNT
                        String sqlamt = "select amount from income where income_id=? and date=?";
                        PreparedStatement pstamt = con.prepareStatement(sqlamt);
                        pstamt.setInt(1, updateId);
                        pstamt.setDate(2, d);
                        ResultSet rs1 = pstamt.executeQuery();
                        while (rs1.next()) {
                            oldAmount = rs1.getDouble("amount");
                        }

                        System.out.print("\t\t\t Enter new amount : ");
                        Double newAmount = sc.nextDouble();

                        ExpenseNote.total = ExpenseNote.total - oldAmount + newAmount;

                        String sql2 = "{call updateAmount(?,?)}";
                        CallableStatement cst2 = con.prepareCall(sql2);
                        cst2.setObject(1, updateId);
                        cst2.setDouble(2, newAmount);
                        int r2 = cst2.executeUpdate();
                        System.out.println((r2 > 0) ? "\t\t\t\tUpdation Successful" : "\t\t\t\tFailed !!!");
                        System.out.println();

                        String sqlbw1 = "select mode,detail from income where user_id=? and income_id=? ";
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
                                d, detail, mode, newAmount, "", ExpenseNote.total));
                        ExpenseNote.bw.flush();
                    }
                    case 3 -> update = false;
                    default -> System.out.println("\t\t\t Invalid choice");
                }
            }
        } else {
            System.out.println(" \t\t\t No Record Found to delete");
        }

    }

    @Override
    public void delete() throws Exception //delete income
    {
        boolean e4 = true;
        String deleteDate = "";
        Date d=null;
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
        System.out.println();
        
        String sql = "{call selectbyDate(?,?)}";
        CallableStatement cst = con.prepareCall(sql);
        cst.setDate(1, d);
        cst.setInt(2, ExpenseNote.id);
        ResultSet rs = cst.executeQuery();
        ArrayDeque<Integer> aa=new ArrayDeque<>();
        int iddd;
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println("\t\t\t ID          Date          Details          Amount          Mode");
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            while (rs.next()) {
                iddd=rs.getInt(1);
                aa.add(iddd);
                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-20s%n",
                        iddd, rs.getDate(2), rs.getString(3), rs.getDouble(4), rs.getString(5));
            }
            System.out.println("\t\t\t ------------------------------------------------------------------------");

            boolean e3 = true;
            int updateId = 0;
            //////
            while (e3) {
                System.out.print("\t\t\t Enter id whose data you want to modify : ");
                updateId = sc.nextInt();
                if (aa.contains(updateId)) {
                    e3 = false;
                } else {
                    System.out.println("Enter valid ID");
                }
            }
            ////////

            String sql2 = "select amount from income where income_id=? and date=?";
            PreparedStatement pst = con.prepareStatement(sql2);
            pst.setInt(1, updateId);
            pst.setDate(2, d);
            ResultSet rs2 = pst.executeQuery();
            while (rs2.next()) {
                amount = rs2.getDouble("amount");
            }

            ExpenseNote.total -= amount;

            String sqlbw = "select mode,detail from income where user_id=? and income_id=? ";
            PreparedStatement pst1 = con.prepareStatement(sqlbw);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, updateId);
            ResultSet rsbw = pst1.executeQuery();
            while (rsbw.next()) {
                mode = rsbw.getString("mode");
                detail = rsbw.getString("detail");

            }
            ExpenseNote.bw.write("Below income is deleted : ");
            ExpenseNote.bw.newLine();

            ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                    d, detail, mode, amount, "", ExpenseNote.total));
            ExpenseNote.bw.flush();

            String time = "select current_timestamp";
            PreparedStatement pst_time = con.prepareStatement(time);
            ResultSet rs_time = pst_time.executeQuery();
            while (rs_time.next()) {
                this.timeStamp = rs_time.getTimestamp(1);
            }
            // LocalDate d1 = d.toLocalDate();
            Income i = new Income(d.toLocalDate(), detail, amount, mode, timeStamp);
            ExpenseNote.deletedData.insertDeletedData(i);

            String sql1 = "{call deleteIncome(?)}";
            CallableStatement cst1 = con.prepareCall(sql1);
            cst1.setInt(1, updateId);
            int r = cst1.executeUpdate();
            System.out.println((r > 0) ? "\t\t\t Record Deleted" : "\t\t\t No Record Deleted");
            System.out.println();
        } else {
            System.out.println("\t\t\t No Record Found to delete");
        }
    }

    @Override
    public void viewTodayReport() throws Exception {
        LocalDate l_date = LocalDate.now();
        String todayDate = l_date.toString();
        String sql = "Select * from income where date=? and user_id=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, todayDate);
        pst.setInt(2, ExpenseNote.id);
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t\t\t\t Today's  Report: ");
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.printf("\t\t\t %-10s %-14s %-17s %-15s %-20s%n",
                    "ID", "DATE", "Detail", "Amount", "Mode");
            System.out.println("\t\t\t ------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("\t\t\t %-10s %-14s %-17s %-15s %-20s%n",
                        rs.getInt(1), rs.getDate(2), rs.getString(3), rs.getDouble(4), rs.getString(5));
            }
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println();
        } else {
            System.out.println("\t\t\t\t No Record Found !!!");
            System.out.println();
        }
    }

    @Override
    public void viewMonthlyReport() throws Exception {
        System.out.print("\t\t\t Enter month (1-12) : ");
        String monthNo = sc.nextLine();
        System.out.print("\t\t\t Enter year : ");
        String year = sc.nextLine();

        String sql = "select * from income where user_id=? and MONTH(date)=? and YEAR(date)=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, ExpenseNote.id);
        pst.setInt(2, Integer.parseInt(monthNo));
        pst.setInt(3, Integer.parseInt(year));
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t\t\t\t Monthly Report for " + monthNo + "/" + year + ":");
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println("\t\t\t ID          Date          Category          Amount          Description");
            System.out.println("\t\t\t ------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-20s%n",
                        rs.getInt(1), rs.getDate(2), rs.getString(3), rs.getDouble(4), rs.getString(5));
            }
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println();
            String sql1 = "select sum(amount) as totalAmount from income where user_id=? and MONTH(date)=? and YEAR(date)=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, Integer.parseInt(monthNo));
            pst1.setInt(3, Integer.parseInt(year));
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                double totalamount = rs1.getDouble("totalAmount");
                System.out.println("\t\t\t Total monthly income : " + totalamount);
                System.out.println();
            }
        } else {
            System.out.println("\t\t\t\t No expense found for this month !!!");
            System.out.println();
        }
    }

    @Override
    public void viewYearlyReport() throws Exception {
        System.out.print("\t\t\t Enter year : ");
        String year = sc.nextLine();

        String sql = "select * from income where user_id=? and YEAR(date)=?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, ExpenseNote.id);
        pst.setInt(2, Integer.parseInt(year));
        ResultSet rs = pst.executeQuery();
        if (rs.isBeforeFirst()) {
            System.out.println("\t\t\t\t\t\t Yearly Report for " + year + ":");
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println("\t\t\t ID          Date          Category          Amount          Description");
            System.out.println("\t\t\t ------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("\t\t\t %-10s %-14s %-17s %-15.2f %-20s%n",
                        rs.getInt(1), rs.getDate(2), rs.getString(3), rs.getDouble(4), rs.getString(5));
            }
            System.out.println("\t\t\t ------------------------------------------------------------------------");
            System.out.println();

            String sql1 = "select sum(amount) as totalAmount from income where user_id=? and YEAR(date)=?";
            PreparedStatement pst1 = con.prepareStatement(sql1);
            pst1.setInt(1, ExpenseNote.id);
            pst1.setInt(2, Integer.parseInt(year));
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                double totalamount = rs1.getDouble("totalAmount");
                System.out.println("\t\t\t Total yearly income : " + totalamount);

            }
            System.out.println();
        } else {
            System.out.println("\\t\\t\\t\\t No income found for this year !!!");
            System.out.println();
        }
    }

    @Override
    public String toString() {
        return "Income [date=" + date + ", detail=" + detail + ", amount=" + amount + ", mode=" + mode + ", timeStamp="
                + timeStamp + "]";
    }
}
