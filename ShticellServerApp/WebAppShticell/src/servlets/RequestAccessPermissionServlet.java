package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.shticell.engine.Engine;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static utils.ServletUtils.getEngine;

@WebServlet(name = "RequestAccessPermissionServlet", urlPatterns = "/requestAccessPermission")

public class RequestAccessPermissionServlet extends HttpServlet {
    private final static String SHEET_NAME = "sheetName";
    private final static String REQUESTED_PERMISSION = "requestedPermission";
    private static final Object lock = new Object();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sheetName = request.getParameter(SHEET_NAME);
        String requestedPermission = request.getParameter(REQUESTED_PERMISSION);

        Engine engine = getEngine(getServletContext());
        try {
            synchronized (lock) {
                engine.requestAccessPermission(sheetName, SessionUtils.getUsername(request), requestedPermission);
                System.out.println("Request access permission for " + SessionUtils.getUsername(request) + " to " + sheetName + " with permission " + requestedPermission);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed request access " + e.getMessage());
            out.flush();
            out.close();
        }
    }

}
