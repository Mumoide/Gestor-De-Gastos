package org.pm.export.service;

import org.pm.export.client.ExpenseConverterClient;
import org.pm.export.dto.ExpenseExportRequestDTO;
import org.pm.model.Expense.Expense;

import java.util.List;
import java.util.stream.Collectors;

public class ExportService {
    private final ExpenseConverterClient client;

    public ExportService(ExpenseConverterClient client) {
        this.client = client;
    }

    /** Maps domain expenses to request DTOs and posts them. */
    public boolean exportToCsvRemote(List<Expense> domainExpenses) {
        List<ExpenseExportRequestDTO> payload = domainExpenses.stream()
                .map(e -> new ExpenseExportRequestDTO(
                        e.getName(),
                        e.getDate(),
                        e.getAmount(),
                        e.getType(),
                        e.getNote()
                ))
                .collect(Collectors.toList());

        try {
            return client.sendExpenses(payload);
        } catch (Exception ex) {
            System.err.println("Export failed: " + ex.getMessage());
            return false;
        }
    }
}
