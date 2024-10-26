package com.shticell.ui.jfx.sheetsManagement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shticell.ui.jfx.utils.Constants.*;

public class ManagementRequests {

    private SheetsManagementController controller;

    public ManagementRequests(SheetsManagementController controller) {
        this.controller = controller;
    }


    protected void uploadFile(File file) throws IOException {
        RequestBody body =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("XMLFile", file.getName(), RequestBody.create(file, MediaType.parse("text/xml")))
                        .build();

        String finalUrl = HttpUrl
                .parse(BASE_URL + UPLOAD_FILE_PAGE)
                .newBuilder()
                .toString();

        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Upload Error", "An error occurred while uploading the file " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();

                            // Print the response - for DEBUG
                            System.out.println(responseBody);

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();

                            SheetDTO sheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.addSheet(sheet , "fakeUserName: in management");
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                        }
                    });
                }
            }
        }, body);

    }

    public void getActiveSheets() {

        String finalUrl = HttpUrl
                .parse(BASE_URL + GET_ALL_SHEETS)
                .newBuilder()
                .toString();

        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Update Sheets Error", "An error occurred while trying to update available sheets " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            Map<String, List<SheetDTO>> sheets = new Gson().fromJson(responseBody, new TypeToken<Map<String, List<SheetDTO>>>(){}.getType());
                            controller.populateSheetsTable(sheets);
                        } catch (Exception e) {
                            controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

}
