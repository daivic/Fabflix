import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet("/movie-suggestion")
public class AutoCompleteServlet extends HttpServlet {
    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    //public static HashMap<Integer, String> superHeroMap = new HashMap<>();

//    static {
//        superHeroMap.put(1, "Blade");
//        superHeroMap.put(2, "Ghost Rider");
//        superHeroMap.put(3, "Luke Cage");
//        superHeroMap.put(4, "Silver Surfer");
//        superHeroMap.put(5, "Beast");
//        superHeroMap.put(6, "Thing");
//        superHeroMap.put(7, "Black Panther");
//        superHeroMap.put(8, "Invisible Woman");
//        superHeroMap.put(9, "Nick Fury");
//        superHeroMap.put(10, "Storm");
//        superHeroMap.put(11, "Iron Man");
//        superHeroMap.put(12, "Professor X");
//        superHeroMap.put(13, "Hulk");
//        superHeroMap.put(14, "Cyclops");
//        superHeroMap.put(15, "Thor");
//        superHeroMap.put(16, "Jean Grey");
//        superHeroMap.put(17, "Wolverine");
//        superHeroMap.put(18, "Daredevil");
//        superHeroMap.put(19, "Captain America");
//        superHeroMap.put(20, "Spider-Man");
//        superHeroMap.put(101, "Superman");
//        superHeroMap.put(102, "Batman");
//        superHeroMap.put(103, "Wonder Woman");
//        superHeroMap.put(104, "Flash");
//        superHeroMap.put(105, "Green Lantern");
//        superHeroMap.put(106, "Catwoman");
//        superHeroMap.put(107, "Nightwing");
//        superHeroMap.put(108, "Captain Marvel");
//        superHeroMap.put(109, "Aquaman");
//        superHeroMap.put(110, "Green Arrow");
//        superHeroMap.put(111, "Martian Manhunter");
//        superHeroMap.put(112, "Batgirl");
//        superHeroMap.put(113, "Supergirl");
//        superHeroMap.put(114, "Black Canary");
//        superHeroMap.put(115, "Hawkgirl");
//        superHeroMap.put(116, "Cyborg");
//        superHeroMap.put(117, "Robin");
//    }

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Output stream to STDOUT
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        try {
            // setup the response json arrray
            Connection dbCon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            query = query.replace(" ", "*+");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            String autoQuery = String.format("SELECT m.id, m.title \n"+
                    "From movies m \n"+
                    "WHERE MATCH (title) AGAINST ('+%1$S*' in boolean mode)\n", query);
                    autoQuery += "ORDER BY m.title ASC;";

            System.out.println(autoQuery);
            PreparedStatement statement = dbCon.prepareStatement(autoQuery);

            ResultSet rs = statement.executeQuery(autoQuery);
            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars


            int i = 0;
            rs.next();
            do {
                String movieTitle = rs.getString("title");
                String movieID = rs.getString("id");
                System.out.println(movieTitle);



                jsonArray.add(generateJsonObject(movieID, movieTitle));
                i++;
            } while(i<10&&rs.next());



//            for (Integer id : superHeroMap.keySet()) {
//                String heroName = superHeroMap.get(id);
//                if (heroName.toLowerCase().contains(query.toLowerCase())) {
//                    jsonArray.add(generateJsonObject(id, heroName));
//                }
//            }
            System.out.println(jsonArray);
            System.out.println(jsonArray.getClass());
            System.out.println(jsonArray.toString().getClass());



            response.getWriter().write(jsonArray.toString());
            rs.close();
            statement.close();
            dbCon.close();
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
