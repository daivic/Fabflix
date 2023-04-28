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

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/form")
public class FormServlet extends HttpServlet {

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

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        out.println("<html><head><title>MovieDBExample: Found Records</title></head>");

        // Building page body
        out.println("<body><h1>MovieDBExample: Found Records</h1>");


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in movielist.html
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            String year = request.getParameter("year");




            // Generate a SQL query (this one is with all of the search variables);
            String query = String.format("SELECT m.title, m.year, m.director\n" +
                    "FROM movies m\n" +
                    "INNER JOIN stars_in_movies sim ON m.id = sim.movieId\n" +
                    "INNER JOIN stars s ON s.id = sim.starId\n" +
                    "WHERE (m.title LIKE '%s' or m.title is null)\n", title);
            query += String.format("AND (m.director LIKE '%1$s' or '%1$s' is null)\n", director);
            query += String.format("AND (s.name LIKE '%1$s' or '%1$s' is null)\n", star);
            query += String.format("AND (m.year = '%1$s' or '%1$s' is null);", year);
            out.println(query);

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

            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>title</td><td>year</td></tr>");
            while (rs.next()) {
                String m_title = rs.getString("title");
                String m_year = rs.getString("year");
                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", m_title, m_year));
            }
            out.println("</table>");


            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();



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
            request.getServletContext().log("Error: ", e);

            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}
