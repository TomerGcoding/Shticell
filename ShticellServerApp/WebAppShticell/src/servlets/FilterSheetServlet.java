package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shticell.engine.Engine;
import dto.SheetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import static utils.ServletUtils.getEngine;

@WebServlet (name = "FilterSheetServlet", urlPatterns = "/filterSheet")
public class FilterSheetServlet extends HttpServlet {
    private final static String SHEET_NAME = "sheetName";
    private static  final String RANGE_TO_FILTER = "rangeToFilter";
    private static final String COLUMNS_TO_FILTER_BY = "columnToFilterBy";

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(SHEET_NAME);
        String range = request.getParameter(RANGE_TO_FILTER);
        String columns = request.getParameter(COLUMNS_TO_FILTER_BY);
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }

        String jsonRequest = jsonBody.toString();
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> values = new Gson().fromJson(jsonRequest, listType);

        Engine engine = getEngine(getServletContext());
        try {
            SheetDTO sheetDTO = engine.filterSheet(sheetName, range, columns, values);
            Type sheetType = new TypeToken<SheetDTO>() {
            }.getType();
            String jsonResponse = new Gson().toJson(sheetDTO, sheetType);
            PrintWriter out = response.getWriter();
            out.write(jsonResponse);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to filter sheet: " + e.getMessage());
            out.flush();
            out.close();
        }
    }
}
