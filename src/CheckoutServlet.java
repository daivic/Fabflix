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
@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {

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
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
        System.out.println("Validating");
        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in movielist.html
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String CCNum = request.getParameter("CCNum");
            String expDate = request.getParameter("expDate");

            // get the previous items in a ArrayList
            String query = String.format("SELECT COUNT(*) FROM creditcards\n" +
                    "WHERE firstName = '%1$s' \n", firstName);
            query += String.format("AND lastName = '%1$s' \n", lastName);
            query += String.format("AND id = '%1$s' \n", CCNum);
            query += String.format("AND expiration = '%1$s';", expDate);


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String isValid = rs.getString("Count(*)");
            //JsonArray jsonArray = new JsonArray();
            //System.out.println(isValid);
            //this is used to insert sale into database
            


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("isValid", isValid);


            rs.close();
            statement.close();
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
