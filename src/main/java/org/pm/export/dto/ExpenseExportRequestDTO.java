package org.pm.export.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.pm.model.Expense.Category;

import java.time.LocalDate;

public class ExpenseExportRequestDTO {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NotBlank
    private LocalDate date;

    @NotBlank
    private int amount;

    @NotBlank
    @Size(min = 1, max = 100)
    private Category type;

    @Size(min = 1, max = 100)
    private String note;

    public ExpenseExportRequestDTO() {
    }

    public ExpenseExportRequestDTO(String name, LocalDate date, int amount, Category type, String note) {

        this.name = name;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() { return date;}

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Category getType() {
        return type;
    }

    public void setType(Category type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
