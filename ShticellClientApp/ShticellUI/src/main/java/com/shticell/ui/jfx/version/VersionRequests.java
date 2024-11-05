package com.shticell.ui.jfx.version;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.shticell.ui.jfx.utils.Constants.*;

public class VersionRequests {

    private final VersionController controller;

    public VersionRequests(VersionController controller) {
        this.controller = controller;
    }

    public void showVersion(String sheetName, int version)
    {
        String finalUrl = HttpUrl
                .parse(BASE_URL +SHOW_VERSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("version", String.valueOf(version))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Filtering Error", "An error occurred while filtering the sheet: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    response.close();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Filtering Error", "An error occurred while filtering the sheet " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            System.out.println(responseBody);
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO sheetVersion = gson.fromJson(responseBody, SheetDTO.class);
                            controller.showSheetPopup(sheetVersion);
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Filtering Error", "An error occurred while filtering the sheet: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }
}

