package servlets;

import java.io.IOException;

import com.shticell.engine.users.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import static constants.Constants.*;

@WebServlet (name = "ShowVersionServlet" , urlPatterns = {"/showVersion"})
public class ShowVersionServlet extends HttpServlet {

    private static final String VERSION = "version";

    @Override
    protected void doGet()


}
