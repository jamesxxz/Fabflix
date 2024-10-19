import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonObject;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieve form data
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("redirect", true);  // Signal the client to redirect
        jsonResponse.addProperty("redirectUrl", "index.html");  // The URL to redirect to
        PrintWriter out = response.getWriter();

        out.write(jsonResponse.toString());

        response.setStatus(200);
        out.close();
    }
}

