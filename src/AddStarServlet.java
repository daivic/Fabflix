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
import java.sql.Statement;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addstar")
public class AddStarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
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
        response.setContentType("application/json"); // Response mime type
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            String star = request.getParameter("star");
            String year = request.getParameter("year");

            System.out.println(star);
            System.out.println(year);


            CallableStatement statement = conn.prepareCall("{call add_star(?, ?)}");

            statement.setString(1, star);
            statement.setString(2, year);

            statement.execute();

            System.out.println("executed");

            ResultSet rs = statement.getResultSet();
            rs.next();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", rs.getString("message"));
            System.out.println("finished");



            out.write(jsonObject.toString());

            statement.close();
            conn.close();
            rs.close();
            response.setStatus(200);


        } catch (Exception e) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", e.getMessage());
            response.setStatus(500);
        }


    }
}
