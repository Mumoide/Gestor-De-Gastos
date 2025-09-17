package org.pm.export.client;

import org.pm.export.config.HttpConfig;
import org.pm.export.dto.ExpenseExportRequestDTO;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;

public class ExpenseConverterClient {
    private final HttpClient http;

    public ExpenseConverterClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(HttpConfig.CONNECT_TIMEOUT)
                .build();
    }

    /** POST /expenses with the given list. Returns true for any 2xx. */
    public boolean sendExpenses(List<ExpenseExportRequestDTO> payload) throws Exception {
        String json = HttpConfig.GSON.toJson(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(resolve("/expenses"))
                .timeout(HttpConfig.REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        int sc = res.statusCode();
        if (sc / 100 == 2) return true;

        throw new RuntimeException("Converter returned " + sc + ": " + res.body());
    }

    private static URI resolve(String path) {
        return HttpConfig.BASE_URI.resolve(path.startsWith("/") ? path : "/" + path);
    }
}
