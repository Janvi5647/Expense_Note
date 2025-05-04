package Project;

import java.sql.Connection;
import java.sql.PreparedStatement;

//diplay deleted data
//delete permanantly
//retrive deleted data
public class RecycleBin {
    public RecycleBin(){}
    static Connection con = DatabaseConnection.getConnection();
    Node first = null;

   public void insertDeletedData(Object o) {
        Node temp = first;
        Node newNode = new Node(o);

        if (first == null) {
            first = newNode;
        } else {
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = newNode;

        }
    }

   public void display() {
        Node temp = first;

        if (first == null) {
            System.out.println();
            System.out.println("\t\t\tNo data in the Recycle Bin");
            System.out.println();

            return;
        }
        System.out.println();
        System.out.println("\t\t\tDeleted Data:~");
        System.out.println();
        while (temp != null) {
            System.out.print(temp.data.toString() + "\n");
            temp = temp.next;
        }

    }

   public void emptyRecycleBin() {
        if (first == null) {
            System.out.println();
            System.out.println("\t\t\tThere is no deleted data.");
            System.out.println();
            return;
        }
        System.out.println();
        System.out.println("\t\t\tData deleted permanently");
        System.out.println();
        first = null;
    }

   public void retrive() throws Exception {
        Node temp = first;
        if (first == null) {
            System.out.println();
            System.out.println("\t\t\t NO Data Found in Recycle Bin");
            System.out.println();
            return;
        }
        System.out.println();
        System.out.println("\t\t\tData Retrived!!!!");
        System.out.println();
        while (temp != null) {
            if (temp.data instanceof Income income) {

                String sql = "Insert into income(date,detail,amount,mode,user_id,timeStamp) values(?,?,?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(sql);

                pst.setObject(1, income.date);
                pst.setString(2, income.detail);
                pst.setDouble(3, income.amount);
                pst.setString(4, income.mode);
                pst.setInt(5, ExpenseNote.id);
                pst.setTimestamp(6, income.timeStamp);
                pst.executeUpdate();

                ExpenseNote.total += income.amount;
                ExpenseNote.bw.write("Income retrived from Recycle Bin");
                ExpenseNote.bw.newLine();
                ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                        income.date, income.detail, income.mode, income.amount, "", ExpenseNote.total));
                ExpenseNote.bw.flush();
            } else {
                String sql1 = "Insert into expense(date,detail,amount,cid,mode,user_id,timeStamp) values(?,?,?,?,?,?,?)";
                PreparedStatement pst1 = con.prepareStatement(sql1);
                Expense expense = (Expense) temp.data;
                pst1.setObject(1, expense.date);
                pst1.setString(2, expense.detail);
                pst1.setDouble(3, expense.amount);
                pst1.setInt(4, expense.categoryId);
                pst1.setString(5, expense.mode);
                pst1.setInt(6, ExpenseNote.id);
                pst1.setTimestamp(7, expense.timeStamp);
                pst1.executeUpdate();

                ExpenseNote.total -= expense.amount;
                ExpenseNote.bw.write("Expense retrived from Recycle Bin");
                ExpenseNote.bw.newLine();
                ExpenseNote.bw.write(String.format("%-14s %-11s %-8s %-11s %-11s %-10s\n",
                        expense.date, expense.detail, expense.mode, "", expense.amount, ExpenseNote.total));
                ExpenseNote.bw.flush();
            }
            temp = temp.next;
        }

    }

    class Node {
        Object data;
        Node next;

        Node(Object data) {
            this.data = data;
            this.next = null;
        }
    }
}
