import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.mysql.cj.conf.ConnectionUrlParser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        HashMap<String, Integer> previousMovies = (HashMap<String, Integer>) session.getAttribute("previousMovies");
        if (previousMovies == null) {
            previousMovies = new HashMap<>();
        }
        // Log to localhost log
        JsonArray previousItemsJsonArray = new JsonArray();
        previousMovies.forEach((key, value) -> previousItemsJsonArray.add(key + "%$%" + value.toString()));
        responseJsonObject.add("previousMovies", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String isAdd = request.getParameter("add");
        System.out.println(isAdd);
        HttpSession session = request.getSession();
        HashMap<String, Integer> previousMovies = (HashMap<String, Integer>) session.getAttribute("previousMovies");





        if (previousMovies == null) {
            previousMovies = new HashMap<>();
            previousMovies.put(title, 1);
            session.setAttribute("previousMovies", previousMovies);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousMovies) {
                if(isAdd == null){
                    if(previousMovies.get(title) == null){
                        previousMovies.put(title, 1);
                    }else{
                        previousMovies.put(title, previousMovies.get(title) + 1);
                    }
                }else if(isAdd.equals("REMOVE")) {
                    previousMovies.put(title, 0);
                    previousMovies.remove(title);
                }else{
                    if(previousMovies.get(title)>1){
                        previousMovies.put(title, previousMovies.get(title) - 1);
                    }else{
                        previousMovies.remove(title);
                    }


                }

                session.setAttribute("previousMovies", previousMovies);


            }
        }

        JsonObject responseJsonObject = new JsonObject();
        JsonArray previousItemsJsonArray = new JsonArray();
        previousMovies.forEach((key, value) -> previousItemsJsonArray.add(key + "%$%" + value.toString()));
        responseJsonObject.add("previousMovies", previousItemsJsonArray);

        response.getWriter().write(responseJsonObject.toString());
    }
}
