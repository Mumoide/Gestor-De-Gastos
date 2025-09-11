package util;

import java.util.ArrayList;
import java.util.List;

public class ConsoleTable {
    private final List<String> headers;
    private final List<List<String>> rows = new ArrayList<>();
    private boolean hasSeparator = false;

    public ConsoleTable(List<String> headers) {
        this.headers = headers;
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public void addSeparator() {
        hasSeparator = true;
    }

    public void print() {
        int cols = headers.size();
        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) widths[i] = headers.get(i).length();
        for (var row : rows) {
            for (int i = 0; i < cols && i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i) == null ? 0 : row.get(i).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        // Header
        for (int i = 0; i < cols; i++) {
            sb.append(pad(headers.get(i), widths[i])).append(i == cols - 1 ? "" : " | ");
        }
        System.out.println(sb);
        // Separator
        sb.setLength(0);
        for (int i = 0; i < cols; i++) {
            sb.append("-".repeat(widths[i])).append(i == cols - 1 ? "" : "-+-");
        }
        System.out.println(sb);
        // Rows
        int printed = 0;
        for (var row : rows) {
            sb.setLength(0);
            for (int i = 0; i < cols; i++) {
                String val = i < row.size() ? row.get(i) : "";
                sb.append(pad(val, widths[i])).append(i == cols - 1 ? "" : " | ");
            }
            System.out.println(sb);
            printed++;
        }
        if (hasSeparator) {
            System.out.println("-".repeat(Math.max(3, String.join(" | ", headers).length())));
        }
    }

    private String pad(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s;
        return s + " ".repeat(width - s.length());
    }
}
