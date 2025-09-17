package org.pm.model.Expense;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private UUID id;
    private String name;
    private LocalDate date;
    private int amount;
    private Category type;
    private String note;
    private boolean planillable;

    public Expense() {
    }

    public Expense(UUID id, String name, LocalDate date, int amount, Category type, String note, boolean planillable) {

        this.id = id;
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.planillable = planillable;
    }

    public UUID getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPlanillable() {
        return planillable;
    }

    public void setPlanillable(boolean planillable) {
        this.planillable = planillable;
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
