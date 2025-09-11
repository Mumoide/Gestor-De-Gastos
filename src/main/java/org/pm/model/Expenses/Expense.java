package org.pm.model.Expenses;

import java.time.LocalDate;
import java.util.UUID;

public class Expense {
    private UUID id;
    private String name;
    private LocalDate date;
    private int amount;
    private String type;
    private String note;


    public Expense() {
    }

    public Expense(UUID id, String name, LocalDate date, int amount, String type, String note) {

        this.id = id;
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.note = note;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
