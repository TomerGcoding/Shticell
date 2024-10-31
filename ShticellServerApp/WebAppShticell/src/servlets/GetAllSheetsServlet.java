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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static utils.ServletUtils.getEngine;

@WebServlet (name = "GetAllSheetsServlet", urlPatterns = "/getAllSheets")
public class GetAllSheetsServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Engine engine = getEngine(getServletContext());

        try {
            Map<String, SheetDTO> allSheets = engine.getAllSheets(SessionUtils.getUsername(request));
            //Map<String, List<SheetDTO>> sheets = engine.getAllSheets(SessionUtils.getUsername(request));
            System.out.println("sheets from getAllSheets servlet: " + allSheets);
            String json = new Gson().toJson(allSheets);
            PrintWriter out = response.getWriter();
            out.write(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to get all sheets: " + e.getMessage());
            out.flush();
            out.close();
        }
    }
}

