package org.pm.repository;


import org.pm.model.Expense.Category;
import org.pm.model.Expense.Expense;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ExpenseRepository {
    void save(String name, LocalDate date, int amount, Category type, String note, boolean isPlanillable);
    ArrayList<Expense> listExpenses();
    void editExpenses();
    void deleteExpenses();
    ArrayList<Expense> getDynamicExpenses();
    void monthlySummary();
}
