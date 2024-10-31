package com.shticell.ui.jfx.sheetOperations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    protected void filterSheetRequest(String sheetName, String range, String columns, List<String> selectedValues) {
        System.out.println("in filterSheetRequest, columns: " + columns);
        String finalUrl = HttpUrl
                .parse(BASE_URL + FILTER_SHEET)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
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
                            response.close();
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
                    response.close();
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
                            controller.updateSheet(sheet);
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

    protected void sortSheetRequest(String sheetName, String range, String columns) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + SORT_SHEET)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
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
                            response.close();
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
                            response.close();
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

    protected void updateCellRequest(String sheetName, String cellId, String cellValue) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + UPDATE_CELL)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
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
                            response.close();
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell " + responseBody);
                        } catch (Exception e) {
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO updatedSheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.updateSheet(updatedSheet);
                            controller.showUpdatedSheet(cellId);
                        } catch (Exception e) {
                            controller.showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }


    protected void dynamicAnalysisRequest(String sheetName, String cellId, String cellValue) {
        String finalUrl = HttpUrl
                .parse(BASE_URL+DYNAMIC_ANALYSIS)
                .newBuilder()
                .addQueryParameter("sheetName",sheetName)
                .addQueryParameter("cellId", cellId)
                .addQueryParameter("cellValue", cellValue)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Analysis Error", "An error occurred while dynamic analysis: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            controller.showErrorAlert("Analysis Error", "An error occurred while dynamic analysis: " + responseBody);
                            ;
                        } catch (Exception e) {
                            controller.showErrorAlert("Analysis Error", "An error occurred while dynamic analysis: " + e.getMessage());;
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();
                            SheetDTO updatedSheet = gson.fromJson(responseBody, SheetDTO.class);
                            controller.setSheet(updatedSheet);
                            controller.showUpdatedSheet(cellId);
                        } catch (Exception e) {
                            controller.showErrorAlert("Analysis Error", "An error occurred while dynamic analysis: " + e.getMessage());;
                        }
                    });
                }
            }
        });
    }
}
