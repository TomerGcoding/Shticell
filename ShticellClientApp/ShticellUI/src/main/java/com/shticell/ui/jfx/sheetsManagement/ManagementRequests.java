package com.shticell.ui.jfx.sheetsManagement;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.shticell.ui.jfx.sheetsManagement.SheetsManagementController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import dto.SheetDTO;
import dto.UserAccessDTO;
import dto.json.MapOfSheetsDeserializer;
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

    protected void uploadFile(File file, UploadCallback callback) throws IOException {
        RequestBody body =
                new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("XMLFile", file.getName(), RequestBody.create(file, MediaType.parse("text/xml")))
                        .build();

        String finalUrl = HttpUrl
                .parse(BASE_URL + UPLOAD_FILE_PAGE)
                .newBuilder()
                .toString();

        // async HTTP request
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                    callback.onUploadFailed(e.getMessage());  // Notify failure
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    response.close();
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + responseBody);
                        callback.onUploadFailed(responseBody);  // Notify failure
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
                                    .create();

                            SheetDTO sheet = gson.fromJson(responseBody, SheetDTO.class);
                            callback.onUploadSuccess(sheet);  // Notify success
                        } catch (Exception e) {
                            controller.showErrorAlert("Upload Error", "An error occurred while uploading the file: " + e.getMessage());
                            callback.onUploadFailed(e.getMessage());  // Notify failure
                        }
                    });
                }
            }
        }, body);
    }

    public void approveAccessPermission(String sheetName, UserAccessDTO accessPermission) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + APPROVE_ACCESS_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("username", accessPermission.getUserName())
                .addQueryParameter("accessPermission", accessPermission.getRequestedAccessPermission())
                .toString();

        // async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Approve access fail", "An error occurred while trying approve access request " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            controller.showErrorAlert("Approve access fail", "error occurred while trying approve access request: " + responseBody);
                        }catch (Exception e){
                            e.printStackTrace();
                            controller.showErrorAlert("Approve access fail", "error occurred while trying approve access request: " + e.getMessage());
                        }});

                }
            }
        });
    }

    public void requestAccessPermission(String sheetName, String requestedPermission) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + REQUEST_ACCESS_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("requestedPermission", requestedPermission)
                .toString();

        // async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Request access fail", "An error occurred while trying to request access permission " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                            controller.showErrorAlert("Request access fail", "error occurred while trying to request access permission: " + responseBody);
                        }catch (Exception e){
                            e.printStackTrace();
                            controller.showErrorAlert("Request access fail", "error occurred while trying to request access permission: " + e.getMessage());
                        }});

                }
            }
        });
        
    }

    public void rejectAccessPermission(String sheetName, UserAccessDTO accessPermission) {
        String finalUrl = HttpUrl
                .parse(BASE_URL + REJECT_ACCESS_PERMISSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .addQueryParameter("username", accessPermission.getUserName())
                .addQueryParameter("accessPermission", accessPermission.getRequestedAccessPermission())
                .toString();

        // async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Reject access fail", "An error occurred while trying reject access request " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        try {
                            String responseBody = response.body().string();
                            response.close();
                        }catch (Exception e){
                            e.printStackTrace();
                            controller.showErrorAlert("Reject access fail", "error occurred while trying reject access request: " + e.getMessage());
                        }});

                }
            }
        });
    }


    public interface UploadCallback {
        void onUploadSuccess(SheetDTO sheet);
        void onUploadFailed(String errorMessage);
    }

    public void getActiveSheets() {
        String finalUrl = HttpUrl
                .parse(BASE_URL + GET_ALL_SHEETS)
                .newBuilder()
                .toString();

        // async
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    controller.showErrorAlert("Update Sheets Error", "An error occurred while trying to update available sheets " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                response.close();

                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + responseBody);
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                       //     System.out.println("get sheets response body (200ok) : " + responseBody);
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(new TypeToken<Map<String, SheetDTO>>() {}.getType(), new MapOfSheetsDeserializer())
                                    .create();
                            Map<String, SheetDTO> allSheets = gson.fromJson(responseBody, new TypeToken<Map<String, SheetDTO>>() {}.getType());

                            // Populate the table with the parsed sheets
                            controller.populateSheetsTable(allSheets);
                       //     controller.refreshSheetsTable();

                        } catch (Exception e) {
                            e.printStackTrace();
                            controller.showErrorAlert("Update Sheets", "An error occurred while trying to update available sheets " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

}
