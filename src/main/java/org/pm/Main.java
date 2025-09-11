package org.pm;

import org.pm.model.Expenses.Expense;
import org.pm.service.ExpenseService;
import util.ConsoleTable;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Scanner SC = new Scanner(System.in);
    private static final ExpenseService expenseService = new ExpenseService();

    public static void main(String[] args) {
        File theFile = new File("data/expenses.json");
        File parent = theFile.getParentFile();

        if (!parent.exists()) {
            parent.mkdir();
        }

        if(!theFile.exists()) { // Create file if it doesnt exist
            System.out.println("El archivo no existe, creando uno nuevo");
            ArrayList<Expense> list = new ArrayList<Expense>();
            ExpenseService.insert(theFile, list);
        }

        boolean running = true;

        while (running){
            showMenu();
            String opt = SC.nextLine().trim();
            switch (opt){
                case "1" -> addExpenseMessage();
                case "2" -> listExpenses();
                case "3" -> System.out.println("Ingresaste a agregar gasto");
                case "4" -> System.out.println("Ingresaste a agregar gasto");
                case "5" -> System.out.println("Ingresaste a agregar gasto");
                case "6" -> System.out.println("Ingresaste a agregar gasto");
                case "0" -> running = false;
                default -> System.out.println("Opcion invalida");
            }
        }

        System.out.println("Gestor de gastos cerrado.");
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("1) Agregar gasto");
        System.out.println("2) Listar gastos (por mes opcional)");
        System.out.println("3) Editar gasto");
        System.out.println("4) Eliminar gasto");
        System.out.println("5) Reporte mensual");
        System.out.println("6) Exportar reporte mensual a CSV");
        System.out.println("0) Salir");
        System.out.print("> ");
    }

    public static void addExpenseMessage(){
        System.out.print("Ingresa el nombre del gasto: ");
        String name = SC.nextLine().trim();
        System.out.print("Ingresa la fecha del gasto (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(SC.nextLine());
        System.out.print("Ingresa la cantidad del gasto: ");
        int amount = Integer.parseInt(SC.nextLine());
        System.out.print("Ingresa el tipo del gasto: ");
        String type = SC.nextLine().trim();
        System.out.print("(Opcional) Ingresa las notas del gasto: ");
        String note = SC.nextLine().trim();

        expenseService.save(name, date, amount, type, note);
        System.out.println("Gasto agregado.");

    }

    public static void listExpenses(){
        List<Expense> list = expenseService.listExpenses();

        // ConsoleTable t = new ConsoleTable(List.of("Fecha", "Monto", "Categoria", "Nota"))

//        System.out.println("|---Fecha---|---Monto---|------Categoria------|--------------Nota--------------|");
//        for (Expense expenses :expenseService.listExpenses()){
//            String row = "|2"+expenses.getDate()+" |---"+expenses.getName()+"---|------"+expenses.getAmount()+"------|--------------"+expenses.getType()+"--------------|";
//            System.out.println(row);
//        }
//        System.out.println("|-----------|-----------|---------------------|--------------------------------|");

        ConsoleTable t = new ConsoleTable(List.of("Fecha", "Monto", "Categoría", "Nota"));
        for (Expense e : list) {
            t.addRow(List.of(
                    e.getDate().toString(),
                    String.valueOf(e.getAmount()),
                    e.getType(),
                    e.getNote() == null ? "" : e.getNote()
            ));
        }
        t.print();

    }

}