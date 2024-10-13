package utils;


import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
//import com.shticell.engine.chat.ChatManager;
import com.shticell.engine.users.UserManager;

import static constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
   // private static final Object chatManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            // Check once inside the synchronized block
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                try {
                    System.out.println("Creating new UserManager");
                    UserManager userManager = new UserManager();
                    System.out.println("UserManager created");
                    servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, userManager);
                    System.out.println("UserManager attribute set in ServletContext");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to create UserManager");
                    throw new RuntimeException("Failed to set UserManager attribute in ServletContext", e);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                    System.out.println("A non-exception error occurred during UserManager creation");
                    throw new RuntimeException("Non-exception error during UserManager creation", t);
                }
            }
        }

        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }


//    public static SheetManager getSheetManager(ServletContext servletContext) {
//        synchronized (chatManagerLock) {
//            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
//                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new SheetManager());
//            }
//        }
//        return (SheetManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
//    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }
}
