package org.pm.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.pm.model.Expense.Category;
import org.pm.model.Expense.Expense;
import org.pm.model.SummaryExpenses.SummaryExpenses;
import org.pm.repository.ExpenseRepository;
import util.ConsoleTable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ExpenseService implements ExpenseRepository {
    static File theFile = new File("data/expenses.json");
    private static Scanner SC = new Scanner(System.in);
    private static final String CREATE_FILE_MSG = "Error al crear el archivo ";

    public void exportJsonToCSVPlanillable() {
        var client = new org.pm.export.client.ExpenseConverterClient();
        var exportService = new org.pm.export.service.ExportService(client);

        ArrayList<Expense> list = listExpenses();

        List<Expense> filtered = list.stream()
                .filter(Expense::isPlanillable)
                .toList();

        boolean ok = exportService.exportToCsvRemote(filtered);
        System.out.println(ok ? "Export OK" : "Export failed");

        list.forEach(e -> e.setPlanillable(false));

        updateExpenses(theFile, list);
        System.out.println("Planillable expenses updated to non planillable.");
    }

    public void exportJsonToCSV() {
        var client = new org.pm.export.client.ExpenseConverterClient();
        var exportService = new org.pm.export.service.ExportService(client);

        List<Expense> list = listExpenses();
        boolean ok = exportService.exportToCsvRemote(list);
        System.out.println(ok ? "Export OK" : "Export failed");
    }


    public void exportToCSVByDate() {
        LocalDate start = readDate("Ingrese la fecha inicial (YYYY-MM-DD): ");
        LocalDate end   = readDate("Ingrese la fecha final  (YYYY-MM-DD): ");

        // if user flipped the range, fix it
        if (end.isBefore(start)) {
            LocalDate tmp = start; start = end; end = tmp;
        }

        var client = new org.pm.export.client.ExpenseConverterClient();
        var exportService = new org.pm.export.service.ExportService(client);

        List<Expense> list = listExpenses();

        LocalDate finalStart = start;
        LocalDate finalEnd = end;
        List<Expense> filtered = list.stream()
                .filter(e -> {
                    LocalDate d = e.getDate();
                    return d != null && !d.isBefore(finalStart) && !d.isAfter(finalEnd); // inclusive
                })
                .toList();
        boolean ok = exportService.exportToCsvRemote(filtered);
        System.out.println(ok ? "Export OK" : "Export failed");
    }

    @Override
    public void monthlySummary() {
        ArrayList<Expense> list = listExpenses();
        Set<Integer> yearsExpenses = new HashSet<>();
        int year = 0;
        Integer month;

        for (Expense e : list) {
            yearsExpenses.add(e.getDate().getYear());
        }
        ArrayList<Integer> yearsList= new ArrayList<>(yearsExpenses);
        year = getUniqueYear(yearsList, "Seleccione el año de los gastos para crear resumen.");
        month = getUniqueMonth(year, list, "Seleccione el mes para crear resumen.");

        ArrayList<Expense> filteredExpenses = getExpensesByMonthAndYear(month, year);

        int totalAmount = filteredExpenses.stream().mapToInt(Expense::getAmount).sum();

        List<Category> listTypes = filteredExpenses.stream().map(Expense::getType).filter(Objects::nonNull).distinct().toList();

        List<SummaryExpenses> summaryExpenses = new ArrayList<>();

        for (Category t: listTypes){
            int typeAmount = filteredExpenses.stream().filter(expense -> expense.getType().equals(t)).mapToInt(Expense::getAmount).sum();
            SummaryExpenses s = new SummaryExpenses(typeAmount,month, year, t.toString());
            summaryExpenses.add(s);
        }

        ConsoleTable t = new ConsoleTable(List.of("Tipo", "Monto"));

        for (SummaryExpenses e : summaryExpenses) {
            t.addRow(List.of(
                    e.getType(),
                    String.valueOf(e.getAmount())
            ));
        }
        t.print();
        System.out.println("Monto total gastado: " + totalAmount);
    }

    @Override
    public void deleteExpenses() {
        ArrayList<Expense> list = listExpenses();
        ArrayList<Expense> filteredExpenses;
        Set<Integer> yearsExpenses = new HashSet<>();
        int year = 0;
        Integer month;

        for (Expense e : list) {
            yearsExpenses.add(e.getDate().getYear());
        }
        ArrayList<Integer> yearsList= new ArrayList<>(yearsExpenses);

        year = getUniqueYear(yearsList, "Seleccione el año de los gastos que quiere eliminar.");
        month = getUniqueMonth(year, list, "Seleccione el mes del gasto que quiere eliminar.");

        filteredExpenses = getExpensesByMonthAndYear(month, year);
        showExpense(filteredExpenses);
        UUID expenseId = obtainFilteredID(filteredExpenses);

        ArrayList<Expense> expenses = getAll();
        for (int i = 0; i < expenses.size(); i++){
            if (expenseId.equals(expenses.get(i).getId())){
                expenses.remove(i);
            }
        }
        updateExpenses(theFile, expenses);
        System.out.println("El gasto ha sido eliminado correctamente.");
    }

    @Override
    public void editExpenses() {
        ArrayList<Expense> list = listExpenses();
        ArrayList<Expense> filteredExpenses;
        Set<Integer> yearsExpenses = new HashSet<>();
        int year = 0;
        Integer month;

        for (Expense e : list) {
            yearsExpenses.add(e.getDate().getYear());
        }
        ArrayList<Integer> yearsList= new ArrayList<>(yearsExpenses);

        year = getUniqueYear(yearsList, "Seleccione el año de los gastos que quiere editar.");
        month = getUniqueMonth(year, list, "Seleccione el mes del gasto que quiere editar.");

        filteredExpenses = getExpensesByMonthAndYear(month, year);
        showExpense(filteredExpenses);
        UUID expenseId = obtainFilteredID(filteredExpenses);

        System.out.print("Ingresa el nombre del gasto: ");
        String name = SC.nextLine().trim();
        System.out.print("Ingresa la fecha del gasto (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(SC.nextLine());
        System.out.print("Ingresa la cantidad del gasto: ");
        int amount = Integer.parseInt(SC.nextLine());
        System.out.print("Ingresa la categoria del gasto: ");
        Category type = promptCategory();

        boolean isPlanillable = planillableValidation();

        System.out.print("(Opcional) Ingresa las notas del gasto: ");
        String note = SC.nextLine().trim();

        ArrayList<Expense> expenses = getAll();
        for (int i = 0; i < expenses.size(); i++){
            if (expenseId.equals(expenses.get(i).getId())){
                Expense expense = new Expense(
                        expenseId,
                        name,
                        date,
                        amount,
                        type,
                        note,
                        isPlanillable);
                expenses.set(i, expense);
            }
        }
        updateExpenses(theFile, expenses);

    }

    public ArrayList<Expense> getDynamicExpenses(){
        System.out.println("---------Seleccione modo de lista ------------");
        System.out.println("1) Listar todo.");
        System.out.println("2) Listar por mes.");

        String opt = SC.nextLine().trim();
        if(opt.equals("1")){
            return getAll();
        }
        ArrayList<Integer> expenses = yearMonth();
        int year = expenses.getFirst();
        int month = expenses.getLast();
        return getExpensesByMonthAndYear(month,year);
    }

    @Override
    public ArrayList<Expense> listExpenses() {
        return getAll();
    }

    @Override
    public void save() {
        addExpense(theFile);
    }

    private static void addExpense(File theFile) {
        try{
            String name = nameValidation();
            LocalDate date = dateValidation();
            int amount = amountValidation();

            Category type = promptCategory();
            boolean isPlanillable = planillableValidation();

            System.out.println("(Opcional) Ingresa las notas del gasto: ");
            String note = SC.nextLine().trim();

            Expense expense = new Expense(UUID.randomUUID(),name, date, amount, type, note, isPlanillable);

            ArrayList<Expense> list = new ArrayList<>();
            list.add(expense);

            Gson gson = customGson();
            ArrayList<Expense> listExpenses = getAll();
            listExpenses.addAll(list);
            FileWriter fileWriter = new FileWriter(theFile);
            gson.toJson(listExpenses, fileWriter);
            fileWriter.close();
            System.out.println("Gasto agregado.");
        } catch (IOException e) {
            System.err.println(CREATE_FILE_MSG + e.getMessage());
        }
    }

    private static void updateExpenses(File theFile, ArrayList<Expense> list) {
        try{
            FileWriter fileWriter = new FileWriter(theFile);
            Gson gson = customGson();
            gson.toJson(list, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(CREATE_FILE_MSG + e.getMessage());
        }
    }

    private static Gson customGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class,
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
    }

    public static void create(File theFile, List<Expense> list) {
        try{
            FileWriter fileWriter = new FileWriter(theFile);
            Gson gson = new Gson();
            gson.toJson(list);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(CREATE_FILE_MSG + e.getMessage());
        }
    }

    private static ArrayList<Expense> getAll(){
        ArrayList<Expense> expenses = null;
        expenses = getExpenses();
        return (expenses != null) ? expenses : new ArrayList<>();
    }

    private static ArrayList<Expense> getExpensesByMonthAndYear(int month, int year) {
        ArrayList<Expense> expenses = null;
        expenses = getExpenses();
        //list.removeIf(e -> e.getDate() == null || e.getDate().getYear() != year);
        expenses.removeIf(expense -> expense.getDate().getYear() != year || expense.getDate().getMonthValue() != month);
        return (expenses != null) ? expenses : new ArrayList<>();
    }

    private static ArrayList<Expense> getExpenses(){
        try {
            FileReader fileReader = new FileReader(theFile);
            Type type = new TypeToken<ArrayList<Expense>>() {
            }.getType();

            Gson gson = customGson();
            return gson.fromJson(fileReader, type);
        } catch (IOException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    private static Integer getUniqueYear(ArrayList<Integer> yearsList, String msg){
        boolean run = true;
        int i = 1;
        Integer year = 0;
        String invalidSelection = "Seleccion invalida, intentelo de nuevo.";
        while (run) {
            i = 1;
            for (Integer e : yearsList) {
                System.out.println(i+". "+e);
                i+=1;
            }
            System.out.println("0. Salir");
            System.out.println(msg);
            String opt = SC.nextLine().trim();
            if (opt.equals("0")) {
                run = false;
                break;
            }
            try {
                if (yearsList.size() < Integer.parseInt(opt)) {
                    System.out.println(invalidSelection);
                }
                if (yearsList.size() >= Integer.parseInt(opt)) {
                    year = yearsList.get(Integer.parseInt(opt) - 1);
                    run = false;
                }
            } catch (NumberFormatException _) {
                System.out.println(invalidSelection);
            }
        }
        return year;
    }

    private static Integer getUniqueMonth(Integer year, ArrayList<Expense> list, String msg){
        boolean run = true;
        Integer month = 0;
        int i = 1;
        SortedMap<Integer, String> monthsExpenses = new TreeMap<>();
        String invalidSelection = "Seleccion invalida, intentelo de nuevo.";
        int finalYear = year;
        list.removeIf(e -> e.getDate() == null || e.getDate().getYear() != year);

        for (Expense e : list) {
            if (e.getDate().getYear() == finalYear) {
                monthsExpenses.put(e.getDate().getMonthValue(), String.valueOf(e.getDate().getMonth()));
            }
        }

        ArrayList<String> monthsList= new ArrayList<>(monthsExpenses.values());
        ArrayList<Integer> monthsListNumber= new ArrayList<>(monthsExpenses.keySet());
        while (run) {
            i=1;
            for (String e : monthsList) {
                System.out.println(i+". "+e.substring(0,1).toUpperCase() + e.substring(1).toLowerCase());
                i+=1;
            }
            System.out.println("0. Salir");
            System.out.println(msg);
            String opt = SC.nextLine().trim();
            if (opt.equals("0")) {
                run = false;
                continue;
            }
            try {
                if (monthsListNumber.size() < Integer.parseInt(opt)) {
                    System.out.println(invalidSelection);
                }
                if (monthsListNumber.size() >= Integer.parseInt(opt)) {
                    run = false;
                    return monthsListNumber.get(Integer.parseInt(opt) - 1);
                }
            } catch (NumberFormatException _) {
                System.out.println(invalidSelection);
            }
        }
        return month;
    }

    private static void showExpense(ArrayList<Expense> list){
        ConsoleTable t = new ConsoleTable(List.of("#", "Fecha", "Monto", "Categoría", "Nota"));

        for (int i = 0; i < list.size(); i++) {
            Expense e = list.get(i);
            t.addRow(List.of(
                    String.valueOf(i + 1),
                    e.getDate().toString(),
                    String.valueOf(e.getAmount()),
                    String.valueOf(e.getType()),
                    e.getNote() == null ? "" : e.getNote()
            ));
        }
        t.print();
    }

    private static UUID obtainFilteredID(ArrayList<Expense> list){
        Integer idSelected = 0;
        System.out.println("Ingresa el ID del gasto que quiere editar.");
        boolean run = true;
        while (run) {
            try{
                idSelected = Integer.parseInt(SC.nextLine().trim())-1;
                run = false;
            } catch (InputMismatchException _) {
                System.out.println("Seleccion invalida");
            }
        }

        return list.get(idSelected).getId();
    }

    private static ArrayList<Integer> yearMonth(){
        ArrayList<Expense> list = getAll();
        ArrayList<Integer> yearMonth = new ArrayList<>();
        Set<Integer> yearsExpenses = new HashSet<>();
        int year = 0;
        Integer month;

        for (Expense e : list) {
            yearsExpenses.add(e.getDate().getYear());
        }
        ArrayList<Integer> yearsList= new ArrayList<>(yearsExpenses);

        year = getUniqueYear(yearsList, "Seleccione el año de los gastos que quiere ver.");
        month = getUniqueMonth(year, list, "Seleccione el mes de los gastos que quiere ver.");
        yearMonth.add(year);
        yearMonth.add(month);
        return yearMonth;
    }

    public static Category promptCategory() {
        while (true) {
            //System.out.println("Categorias");
            //for (Category c : Category.values()) {
            //    System.out.println(c);
            //}
            System.out.println("Categorías: " + Arrays.toString(Category.values()));
            //System.out.print("Elige categoría [q para cancelar]: ");
            String s = SC.nextLine().trim();
            //if (s.equalsIgnoreCase("q")) return null;
            try {
                return Category.valueOf(s.toUpperCase(Locale.ROOT));
            } catch (Exception _) {
                System.out.println("Categoría inválida.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine().trim();
            try {
                return LocalDate.parse(s); // expects YYYY-MM-DD
            } catch (DateTimeParseException ex) {
                System.out.println("Formato inválido. Usa YYYY-MM-DD (ej: 2025-08-12).");
            }
        }
    }

    private static LocalDate dateValidation(){
        while (true) {
            try {
                System.out.println("Ingresa la fecha del gasto (YYYY-MM-DD): ");
                String date = SC.nextLine();
                date = date.replace("/", "-");
                return LocalDate.parse(date);
            } catch (DateTimeParseException e){
                System.out.println("Fecha inválida, favor ingresar formato YYYY-MM-DD");
            }
        }
    }

    private static int amountValidation(){
        while (true) {
            try{
                System.out.println("Ingresa la cantidad del gasto: ");

                int amount = Integer.parseInt(SC.nextLine());
                if (amount <1){System.out.println("Monto inválido, debe ser un número mayor a 0."); continue;}
                return amount;
            } catch (NumberFormatException _){
                System.out.println("Monto inválido, debe ser un número mayor a 0.");
            }
        }
    }

    private static String nameValidation() {
        while(true){
            try{
                System.out.println("Ingresa el nombre del gasto: ");
                String name = SC.nextLine().trim();
                if (!name.isEmpty()){return name;}
                System.out.println("Favor ingresar un valor.");
            } catch (Exception e){
                System.out.println("Se genero un error con el ingreso del gasto.");
            }

        }
    }

    private static boolean planillableValidation(){
        while (true) {
            boolean isPlanillable;
            System.out.println("El gasto es planillable?");
            System.out.println("1) Si");
            System.out.println("2) No");
            String opt = SC.nextLine().trim();

            if ("1".equals(opt)) { isPlanillable = true; return isPlanillable; }
            if ("2".equals(opt)) { isPlanillable = false; return isPlanillable; }

            System.out.println("Opcion invalida");
        }
    }
}
