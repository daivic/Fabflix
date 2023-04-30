import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        //out.println("<html><head><title>MovieDBExample: Found Records</title></head>");

        // Building page body
        //out.println("<body><h1>MovieDBExample: Found Records</h1>");


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in movielist.html
            String title = "%" +request.getParameter("title")+"%";

            String director = "%"+request.getParameter("director")+"%";
            String star = "%"+request.getParameter("star")+"%";
            String year = request.getParameter("year");
            String results = request.getParameter("results");
            String offset = request.getParameter("offset");
            String order = request.getParameter("order");



            //System.out.println(2004);


            // Generate a SQL query (this one is with all of the search variables);
            String query = String.format("SELECT m.id, m.title, m.year, m.director,\n" +
                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name SEPARATOR ', '), ',', 3) AS genre,\n" +
                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY s.name SEPARATOR ', '), ',', 3) AS star,\n" +
                    "r.rating, \n" +
                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY s.name SEPARATOR ','), ',', 3) AS starId, \n"  +
                    "SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.id ORDER BY g.name SEPARATOR ','), ',', 3) AS genreId\n" +
                    "FROM movies m\n"+
            "JOIN genres_in_movies gm ON m.id = gm.movieId\n"+
            "JOIN genres g ON gm.genreId = g.id\n"+
            "JOIN stars_in_movies sm ON m.id = sm.movieId\n"+
            "JOIN stars s ON sm.starId = s.id\n"+
            "JOIN ratings r ON m.id = r.movieId\n"+
                    "WHERE (m.title LIKE '%1$s' or '%1$s' = '')\n", title);
            query += String.format("AND (m.director LIKE '%1$s' or '%1$s' = '')\n", director);
            query += String.format("AND (s.name LIKE '%1$s' or '%1$s' = '')\n", star);
            query += String.format("AND (m.year = '%1$s' or '%1$s' = '')", year);
            query += "GROUP BY m.id\n" + String.format("ORDER BY '%1$s'\n", order);
            query += String.format("LIMIT %1$s\n", results);
            query += String.format("OFFSET %1$s;", offset);

//            String query = String.format("SELECT m.title, m.year, m.director\n" +
//                    "FROM movies m\n" +
//                    "INNER JOIN stars_in_movies sim ON m.id = sim.movieId\n" +
//                   "INNER JOIN stars s ON s.id = sim.starId\n" +
//                    "WHERE (m.title LIKE '%%'or m.title is null)\n"+
//            "AND (m.director LIKE '%%' or m.director is null)\n"+
//            "AND (s.name LIKE '%s' or s.name is null)\n"+
//            "AND (m.year = 2004 or m.year is null);", name);

//            String query = "SELECT m.title, m.year, m.director\n" +
//                    "FROM movies m\n" +
//                    "INNER JOIN stars_in_movies sim ON m.id = sim.movieId\n" +
//                    "INNER JOIN stars s ON s.id = sim.starId\n" +
//                    "WHERE (m.title LIKE '%Terminal%'or m.title is null)\n" +
//                    "AND (m.director LIKE '%%' or m.director is null)\n" +
//                    "AND (s.name LIKE '%Tom Hanks%' or s.name is null)\n" +
//                    "AND (m.year = 2004 or m.year is null);";


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            // Create a html <table>
            //out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            //out.println("<tr><td>title</td><td>year</td><td>director</td><td>stars</td></tr>");
            while (rs.next()) {

                String movieTitle =  rs.getString("title");
                int movieYear= rs.getInt("year");
                String movieDir= rs.getString("director");
                String movieGenres = rs.getString("genre");
                String movieStars =  rs.getString("star");
                float movieRating =  rs.getFloat("rating");
                String starIds  = rs.getString("starId");
                String movieId  = rs.getString("id");
                String genreIds = rs.getString("genreId");


                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_ids", starIds);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_dir", movieDir);
                jsonObject.addProperty("movie_genres", movieGenres);
                jsonObject.addProperty("movie_stars", movieStars);
                jsonObject.addProperty("movie_rating", movieRating);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("genre_ids", genreIds);

                jsonArray.add(jsonObject);

//                String m_title = rs.getString("title");
//                String m_year = rs.getString("year");
//                String m_dir = rs.getString("director");
//                String m_stars = rs.getString("star");
//                out.println(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", m_title, m_year, m_dir, m_stars));
            }
            //out.println("</table>");


            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);



        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            //request.getServletContext().log("Error: ", e);

            // Output Error Massage to html
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);

            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}
