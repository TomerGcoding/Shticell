package com.shticell.ui.jfx.sheetsManagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.MapOfSheetsDeserializer;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static com.shticell.ui.jfx.utils.Constants.BASE_URL;
import static com.shticell.ui.jfx.utils.Constants.GET_ALL_SHEETS;

public class SheetsRefresher extends TimerTask {

    private final SheetsManagementController controller;
    private final Consumer<Map<String, SheetDTO>> responseConsumer;

    public SheetsRefresher(SheetsManagementController controller, Consumer<Map<String, SheetDTO>> responseConsumer) {
        this.controller = controller;
        this.responseConsumer = responseConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(BASE_URL + GET_ALL_SHEETS)
                .newBuilder()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        try {
            Response response = HttpClientUtil.getClient().newCall(request).execute();
            if (response.code() != 200) {
                System.err.println("Failed to fetch sheets: " + response.message());
                response.close();
                return;
            }

            String responseBody = response.body().string();
            response.close();


            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<Map<String, SheetDTO>>() {}.getType(), new MapOfSheetsDeserializer())
                    .create();
            Map<String, SheetDTO> allSheets = gson.fromJson(responseBody, new TypeToken<Map<String, SheetDTO>>() {}.getType());
            System.out.println("Fetched sheets from refresher: " + allSheets);
            Platform.runLater(() -> {
                controller.populateSheetsTable(allSheets);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
