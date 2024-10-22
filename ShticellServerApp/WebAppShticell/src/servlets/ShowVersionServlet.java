package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shticell.engine.Engine;
import com.shticell.engine.users.UserManager;
import dto.SheetDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import static constants.Constants.*;
import static utils.ServletUtils.getEngine;

@WebServlet (name = "ShowVersionServlet" , urlPatterns = {"/showVersion"})
public class ShowVersionServlet extends HttpServlet {

    private static final String VERSION = "version";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
       try {
           int version = Integer.parseInt(request.getParameter(VERSION));
           Engine engine = getEngine(getServletContext());
           SheetDTO sheetDTO = engine.showChosenVersion(version);
           Type sheetType = new TypeToken<SheetDTO>() {
           }.getType();
           String json = new Gson().toJson(sheetDTO, sheetType);
           response.setContentType("text/plain;charset=UTF-8");
           PrintWriter out = response.getWriter();
           out.write(json);
           out.flush();
           out.close();
           response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to get the chosen version because"  + e.getMessage());
            out.flush();
            out.close();
        }
    }
}

