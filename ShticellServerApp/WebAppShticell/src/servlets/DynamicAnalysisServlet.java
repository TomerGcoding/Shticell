package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DynamicAnalysisServlet", urlPatterns = "/dynamicAnalysis")
public class DynamicAnalysisServlet extends HttpServlet {
    private static final String SHEET_NAME = "sheetName";
    private static final String MAX_VALUE = "maxValue";
    private static final String MIN_VALUE = "minValue";
    private static final String STEP_SIZE = "stepSize";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
    }

}
