package org.pm.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.pm.model.Expenses.Expense;
import org.pm.repository.ExpenseRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class ExpenseService implements ExpenseRepository {
    File theFile = new File("data/expenses.json");

    public ArrayList<Expense> listExpenses() {
        ArrayList<Expense> expenses = null;
        try {
            FileReader fileReader = new FileReader(theFile);
            Type type = new TypeToken<ArrayList<Expense>>() {
            }.getType();

            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
                    new TypeAdapter<LocalDate>() {
                        @Override
                        public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
                            jsonWriter.value(localDate.toString());
                        }

                        @Override
                        public LocalDate read(JsonReader jsonReader) throws IOException {
                            return LocalDate.parse(jsonReader.nextString());
                        }
                    }).create();
            expenses = gson.fromJson(fileReader, type);
        } catch (IOException e) {
            System.out.println(e);
        }
        return expenses;
    }

    @Override
    public void save(String name, LocalDate date, int amount, String type, String note) {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo.");
        }
        Expense expense = new Expense(UUID.randomUUID(),name, date, amount, type, note);
        ArrayList<Expense> list = new ArrayList<>();
        list.add(expense);
        addExpense(theFile, list);
    }

    public static void addExpense(File theFile, ArrayList<Expense> list) {
        try{
            FileWriter fileWriter = new FileWriter(theFile);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
                    new TypeAdapter<LocalDate>(){
                        @Override
                        public void write(JsonWriter jsonWriter, LocalDate localDate)  throws IOException {
                            jsonWriter.value(localDate.toString());
                        }
                        @Override
                        public LocalDate read(JsonReader jsonReader) throws IOException {
                            return LocalDate.parse(jsonReader.nextString());
                        }
                    }).create();
            gson.toJson(list, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error al crear el archivo " + e.getMessage());
        }
    }

    public static void insert(File theFile, ArrayList<Expense> list) {
        try{
            FileWriter fileWriter = new FileWriter(theFile);
            Gson gson = new Gson();
            gson.toJson(list);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error al crear el archivo " + e.getMessage());
        }
    }
}
