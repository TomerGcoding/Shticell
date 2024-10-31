package servlets;

import com.google.gson.Gson;
import com.shticell.engine.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import static utils.ServletUtils.getEngine;

@WebServlet (name = "ApproveAccessPermissionServlet", urlPatterns = "/approveAccessPermission")
public class ApproveAccessPermissionServlet extends HttpServlet {
    private final static String SHEET_NAME = "sheetName";
    private final static String USERNAME = "username";
    private final static String ACCESS_PERMISSION = "accessPermission";
    private static final Object lock = new Object();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(SHEET_NAME);
        String username = request.getParameter(USERNAME);
        String accessPermission = request.getParameter(ACCESS_PERMISSION);

        Engine engine = getEngine(getServletContext());
        try {
            synchronized (lock) {
                engine.approveAccessPermission(SessionUtils.getUsername(request), sheetName, username, accessPermission);}

            response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                PrintWriter out = response.getWriter();
                out.write("Failed approve access " + e.getMessage());
                out.flush();
                out.close();
            }
    }

}
