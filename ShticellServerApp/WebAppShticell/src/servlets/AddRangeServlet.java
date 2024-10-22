package servlets;

import com.google.gson.Gson;
import com.shticell.engine.Engine;
import dto.RangeDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import static utils.ServletUtils.getEngine;

@WebServlet (name = "AddRangeServlet", urlPatterns = "/addRange")
public class AddRangeServlet extends  HttpServlet {
     private final static String RANGE = "range";
     private final static String RANGE_CELLS = "rangeCells";

     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         String newRange = req.getParameter(RANGE);
         String rangeCells = req.getParameter(RANGE_CELLS);
         Engine engine = getEngine(getServletContext());
         try {
             RangeDTO rangeDTO = engine.addRange(newRange, rangeCells);
             String json = new Gson().toJson(rangeDTO, RangeDTO.class);
             resp.setContentType("text/plain;charset=UTF-8");
             PrintWriter out = resp.getWriter();
             out.write(json);
             out.flush();
             out.close();
         }
         catch (Exception e) {
             resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
             PrintWriter out = resp.getWriter();
             out.write("Failed to add range: " + e.getMessage());
             out.flush();
             out.close();
         }
     }

}
