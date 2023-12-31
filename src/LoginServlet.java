import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;





@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("post entered");
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("finished get params");
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        PrintWriter out = response.getWriter();


        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource
            Connection dbCon = dataSource.getConnection();
            // Declare a new statement
            Statement statement = dbCon.createStatement();
            String query = String.format("SELECT * from customers where email='%s'", email);

            ResultSet rs = statement.executeQuery(query);

            boolean success = false;
            if(password != null) {
                if (rs.next()) {
                    // get the encrypted password from the databas
                    System.out.println(password + " gay ");
                    String serverPass = rs.getString("password");
                    System.out.println(password + " gay " + serverPass);
                    if (password.equals(serverPass)) {
                        success = true;
                    }
                    //success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);


                }
            }
            JsonObject responseJsonObject = new JsonObject();




            rs.close();



            if(success){
                // Login success:

                // set this user into the session

                request.getSession().setAttribute("user", new User(email));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.

                responseJsonObject.addProperty("message", "incorrect username or password");

            }
            //String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            //.out.println("gRecaptchaResponse=" + gRecaptchaResponse);



            // Verify reCAPTCHA
            try {
                //RecaptchaVeriUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                //responseJsonObject.addProperty("status", "ReCaptchaFail");
                //responseJsonObject.addProperty("message", e.getMessage());

                //response.getWriter().write(responseJsonObject.toString());

                return;
            }
            System.out.println(responseJsonObject.toString());
            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            // Write error message JSON object to output

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

    }
}
