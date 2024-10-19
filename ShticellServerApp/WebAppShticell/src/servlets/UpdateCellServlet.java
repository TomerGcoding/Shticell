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
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import static utils.ServletUtils.getEngine;

@WebServlet(name = "UpdateCellServlet", urlPatterns = {"/updateCell"})
public class UpdateCellServlet extends HttpServlet {
    private static final String CELL_ID = "cellId";
    private static final String CELL_NEW_VALUE = "cellValue";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String cellId = request.getParameter(CELL_ID);
        String cellValue = request.getParameter(CELL_NEW_VALUE);
        Engine engine = getEngine(getServletContext());

        try {
            System.out.println("trying to update cell in server");
            engine.setCell(cellId, cellValue);
            SheetDTO sheetDTO = engine.showSheet();
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


