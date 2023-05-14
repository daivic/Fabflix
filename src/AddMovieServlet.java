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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("started dopost");

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("started try");

            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String genre = request.getParameter("genre");
            String star = request.getParameter("star");
            String year = request.getParameter("year");

            System.out.println("started addmovie");

            CallableStatement statement = conn.prepareCall("{call add_movie(?, ?, ?, ?, ?)}");
            statement.setString(1, title);
            statement.setString(2, director);
            statement.setString(3, genre);
            statement.setString(4, star);
            statement.setString(5, year);

            System.out.println(statement);

            statement.execute();
            ResultSet rs = statement.getResultSet();

            System.out.println("executed");
            System.out.println(rs);

            rs.next();

            System.out.println(rs);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", rs.getString("message"));

            System.out.println(jsonObject.toString());




            out.write(jsonObject.toString());

            statement.close();
            conn.close();
            rs.close();
            response.setStatus(200);

        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            response.setStatus(500);
        }


    }
}
