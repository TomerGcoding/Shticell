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
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import static utils.ServletUtils.getEngine;

@WebServlet(name = "UpdateCellServlet", urlPatterns = {"/updateCell"})
public class UpdateCellServlet extends HttpServlet {
    private final static String SHEET_NAME = "sheetName";
    private static final String CELL_ID = "cellId";
    private static final String CELL_NEW_VALUE = "cellValue";
    private static final Object lock = new Object();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String sheetName = request.getParameter(SHEET_NAME);
        String cellId = request.getParameter(CELL_ID);
        String cellValue = request.getParameter(CELL_NEW_VALUE);
        Engine engine = getEngine(getServletContext());

        try {
            System.out.println("trying to update cell in server");
            synchronized (lock) {
                engine.setCell(sheetName, cellId, cellValue, SessionUtils.getUsername(request));}
            SheetDTO sheetDTO = engine.showSheet(sheetName, SessionUtils.getUsername(request));
            Type sheetType = new TypeToken<SheetDTO>() {}.getType();
            String json = new Gson().toJson(sheetDTO, sheetType);
            PrintWriter out = response.getWriter();
            out.write(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to update cell: " + e.getMessage());
            out.flush();
            out.close();
        }
    }
}


