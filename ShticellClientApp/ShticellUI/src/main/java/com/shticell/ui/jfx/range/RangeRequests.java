package com.shticell.ui.jfx.range;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.RangeDTO;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.shticell.ui.jfx.utils.Constants.*;

public class RangeRequests {

    private final RangeController controller;
    public RangeRequests(RangeController controller) {
        this.controller = controller;
    }

    protected void addRangeRequest(String sheetName, String rangeName, String rangeCells) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + ADD_RANGE)
                .newBuilder()
                .addQueryParameter("sheetName",sheetName)
                .addQueryParameter("range", rangeName)
                .addQueryParameter("rangeCells", rangeCells)
                .build()
                .toString();


        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Adding Range Error", "An error occurred while adding new range: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Adding Range Error", "An error occurred while adding new range: " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            System.out.println(responseBody);
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            RangeDTO range = gson.fromJson(responseBody, RangeDTO.class);
                            controller.showRange(range);
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Adding Range Error", "An error occurred while adding new range: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    protected void deleteRangeRequest (String sheetName, String rangeName) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + DELETE_RANGE)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("range", rangeName)
                .build()
                .toString();

        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Deleting Range Error", "An error occurred while deleting range: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Deleting Range Error", "An error occurred while deleting range: " + responseBody);
                    });
                }
            }
        });
    }
}

