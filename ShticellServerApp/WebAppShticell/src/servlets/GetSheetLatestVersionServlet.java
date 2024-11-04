package servlets;

import com.google.gson.Gson;
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
import java.util.Map;

import static utils.ServletUtils.getEngine;

@WebServlet(name = "GetSheetLatestVersionServlet", urlPatterns = "/getLatestVersion")
public class GetSheetLatestVersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Engine engine = getEngine(getServletContext());

        try {
            SheetDTO newestVersion = engine.getSheetLatestVersion(SessionUtils.getUsername(request), request.getParameter("sheetName"));
            System.out.println("Latest sheet version from getLatestVersion servlet: " + newestVersion);
            String json = new Gson().toJson(newestVersion);
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

