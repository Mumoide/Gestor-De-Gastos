package org.pm.model.SummaryExpenses;

public class SummaryExpenses {
        int amount;
        int month;
        int year;
        String type;

        public SummaryExpenses(){}

        public SummaryExpenses(int amount, int month, int year, String type) {
            this.amount = amount;
            this.month = month;
            this.year = year;
            this.type = type;
        }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
