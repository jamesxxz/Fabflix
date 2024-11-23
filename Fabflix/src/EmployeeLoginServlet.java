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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Objects;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "EmployeeLoginServlet", urlPatterns = "/_dashboard/api/login")
public class EmployeeLoginServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slavedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        // Retrieve form data
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        JsonObject responseJsonObject = new JsonObject();
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please complete reCAPTCHA verification.");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String email = "";
        String encryptedPassword = "";
        boolean isSuccess = false;
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * from employees where email = ? ";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
                encryptedPassword = rs.getString("password");
                isSuccess = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            }

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            request.getServletContext().log("Error:", e);

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }


        if (isSuccess && !Objects.equals(password, "")) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", new User("", username));

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (Objects.equals(email, "")) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else if (!isSuccess) {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}

