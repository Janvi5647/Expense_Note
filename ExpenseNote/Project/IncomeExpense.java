package Project;

public interface IncomeExpense {
    public void add() throws Exception;

    public void update() throws Exception;

    public void delete() throws Exception;

    public void viewTodayReport() throws Exception;

    public void viewMonthlyReport() throws Exception;

    public void viewYearlyReport() throws Exception;
}

class Main {
    public static void main(String[] args) throws Exception {
        ExpenseNote.expenseNote();
    }
}
