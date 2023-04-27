import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "StarsServlet", urlPatterns = "/api/stars")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT m.id, m.title, m.year, m.director, substring_index(group_concat(distinct g.name ORDER BY g.name SEPARATOR ', '), ',', 3) AS genre,\n " +
                    "substring_index(GROUP_CONCAT(s.name ORDER BY g.name SEPARATOR ', '), ',', 3) AS star, r.rating," +
                    "substring_index(GROUP_CONCAT(s.id ORDER BY g.name SEPARATOR ','), ',', 3) AS starId \n" +
                    "FROM movies m \n" +
                    "JOIN ratings r ON m.id = r.movieId \n" +
                    "JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                    "JOIN genres g ON gm.genreId = g.id \n" +
                    "JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                    "JOIN stars s ON sm.starId = s.id \n" +
                    "Group by m.id \n"+
                    "ORDER BY r.rating DESC\n" +
                    "LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {


                String movieTitle =  rs.getString("title");
                int movieYear= rs.getInt("year");
                String movieDir= rs.getString("director");
                String movieGenres = rs.getString("genre");
                String movieStars =  rs.getString("star");
                float movieRating =  rs.getFloat("rating");
                String starIds  = rs.getString("starId");
                String movieId  = rs.getString("id");


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


                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
