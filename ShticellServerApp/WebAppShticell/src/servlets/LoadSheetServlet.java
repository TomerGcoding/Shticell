package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shticell.engine.Engine;
import dto.SheetDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import utils.SessionUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import static utils.ServletUtils.getEngine;

@WebServlet(name = "SheetLoadServlet", urlPatterns = {"/loadSheet"})
@MultipartConfig
public class LoadSheetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        File xmlFile = getXMLFile(request);
        if (xmlFile == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            out.write("No XML file was uploaded");
            out.flush();
            out.close();  // Close the writer after writing
            return;
        }

        Engine engine = getEngine(getServletContext());
        try {
            // Load the sheet and send response as JSON
            SheetDTO sheetDTO = engine.loadSheetFile(xmlFile.getAbsolutePath(), SessionUtils.getUsername(request));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");  // Set encoding
            PrintWriter out = response.getWriter();
            Type sheetType = new TypeToken<SheetDTO>() {}.getType();
            String json = new Gson().toJson(sheetDTO, sheetType);
           // String json  = new Gson().toJson(sheetDTO);
            System.out.println(json);
            out.write(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.write("Failed to load sheet file: " + e.getMessage());
            out.flush();
            out.close();
        } finally {
            // Clean up: Delete the temporary file if needed
            if (xmlFile != null && xmlFile.exists()) {
                xmlFile.delete();
            }
        }
    }


    private File getXMLFile(HttpServletRequest request) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            if (part.getName().equals("XMLFile")) {
                // Create a temporary file for this upload
                Path tempFile = Files.createTempFile("uploaded-", ".xml");
                File xmlFile = tempFile.toFile();

                try (InputStream fileContent = part.getInputStream()) {
                    // Copy the uploaded file content to the temporary file
                    Files.copy(fileContent, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }

                return xmlFile;
            }
        }
        throw new IOException("No XML file was uploaded");
    }
}
