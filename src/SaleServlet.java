import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;



/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SaleServlet", urlPatterns = "/api/sale")
public class SaleServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        System.out.println("posting");
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in movielist.html
            String CCNum = request.getParameter("CCNum");
            String movieName = request.getParameter("movieName");
            System.out.println(CCNum + movieName);
            // get the previous items in a ArrayList

            // Log to localhost log

            // Perform the query

            //JsonArray jsonArray = new JsonArray();
            //this is used to insert sale into database
        java.util.Date today = new java.util.Date();
        DateFormat dateFormatting = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatting.format(today).toString();

        String insertQuery = String.format("INSERT INTO sales (customerId, movieId, saleDate)\n" +
                "SELECT c.id, m.id, '%1$s'\n", date);
        insertQuery += String.format("FROM customers c, movies m\n" +
                "WHERE c.ccID = '%1$s'", CCNum);
        insertQuery += String.format(" AND m.title = '%1$s';", movieName);
        System.out.println(insertQuery);
        PreparedStatement statement2 = dbCon.prepareStatement(insertQuery);
        int test = statement2.executeUpdate();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("result", test);
        statement2.close();
        dbCon.close();
        out.write(jsonObject.toString());
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
        } finally {
            out.close();
        }
    }
}
