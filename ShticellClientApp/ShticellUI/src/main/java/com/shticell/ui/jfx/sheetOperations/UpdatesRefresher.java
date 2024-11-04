package com.shticell.ui.jfx.sheetOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shticell.ui.jfx.sheetsManagement.SheetsManagementController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.MapOfSheetsDeserializer;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static com.shticell.ui.jfx.utils.Constants.*;

public class UpdatesRefresher extends TimerTask {

    private final SheetOperationController controller;

    public UpdatesRefresher(SheetOperationController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(BASE_URL + GET_LATEST_VERSION)
                .newBuilder()
                .addQueryParameter("sheetName", controller.getSheet().getSheetName())
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        try {
            // Sending the synchronous request
            Response response = HttpClientUtil.getClient().newCall(request).execute();
            if (response.code() != 200) {
                System.err.println("Failed to get latest version : " + response.message());
                response.close();
                return;
            }

            String responseBody = response.body().string();
            response.close();

            // Parsing the response
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<SheetDTO>() {}.getType(), new SheetDTODeserializer())
                    .create();
            SheetDTO latestVersion = gson.fromJson(responseBody, new TypeToken<SheetDTO>() {}.getType());
            System.out.println("Fetched latest version from refresher: " + latestVersion);
            // Using the consumer to handle the response
            Platform.runLater(() -> {
                if(latestVersion.getCurrVersion()>controller.getSheet().getCurrVersion()) {
                    controller.newVersionAvailable();
                }
                else{
                    controller.noNewVersion();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

