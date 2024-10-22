package com.shticell.ui.jfx.sheetOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.shticell.ui.jfx.utils.Constants.*;

public class SheetRequests {

    private SheetOperationController controller;

    public void setController(SheetOperationController controller) {
        this.controller = controller;
    }

    protected void filterSheetRequest(String range, String columns, List<String> selectedValues) {
        System.out.println("in filterSheetRequest, columns: " + columns);
        String finalUrl = HttpUrl
                .parse(BASE_URL + FILTER_SHEET)
                .newBuilder()
                .addQueryParameter("rangeToFilter", range)
                .addQueryParameter("columnToFilterBy", columns)
                .build()
                .toString();

        Gson gsn = new Gson();

        String json = gsn.toJson(selectedValues);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        //async
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
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Filtering Error", "An error occurred while filtering the sheet " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            System.out.println(responseBody);
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO filteredSheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.showFilteredSheetDialog(filteredSheet);
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Filtering Error", "An error occurred while filtering the sheet: " + e.getMessage());
                        }
                    });
                }
            }
        }, body);
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

                            // Print the response - for DEBUG
                            System.out.println(responseBody);

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();

                            SheetDTO sheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.setSheet(sheet);
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

    protected void sortSheetRequest(String range, String columns) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + SORT_SHEET)
                .newBuilder()
                .addQueryParameter("rangeToSort", range)
                .addQueryParameter("columnsToSortBy", columns)
                .build()
                .toString();

        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Sorting Error", "An error occurred while filtering the sheet: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            controller.showErrorAlert("Sortin Error", "An error occurred while filtering the sheet " + responseBody);
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Sorting Error", "An error occurred while filtering the sheet: " + e.getMessage());
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO sortedSheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.showSortedSheetDialog(sortedSheet);
                        }
                        catch (Exception e)
                        {
                            controller.showErrorAlert("Sorting Error", "An error occurred while filtering the sheet: " + e.getMessage());
                        }
                    });
                }
            }
        });


      }

    protected void updateCellRequest(String cellId, String cellValue) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + UPDATE_CELL)
                .newBuilder()
                .addQueryParameter("cellId", cellId)
                .addQueryParameter("cellValue", cellValue)
                .build()
                .toString();

        //async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell " + responseBody);
                        } catch (Exception e) {
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO updatedSheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.setSheet(updatedSheet);
                            controller.showUpdatedSheet(cellId);

                        } catch (Exception e) {
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }
}
