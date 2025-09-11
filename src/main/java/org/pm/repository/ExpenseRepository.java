package org.pm.repository;


import org.pm.model.Expenses.Expense;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ExpenseRepository {
    void save(String name, LocalDate date, int amount, String type, String note);
    ArrayList<Expense> listExpenses();
}
