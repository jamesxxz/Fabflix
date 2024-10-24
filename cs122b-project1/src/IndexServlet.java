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

@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            log("Error during DataSource lookup:", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT name FROM genres ORDER BY name ASC")) {

            JsonArray genresArray = new JsonArray();
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObject genreObject = new JsonObject();
                genreObject.addProperty("genre", rs.getString("name"));
                genresArray.add(genreObject);
            }

            out.write(genresArray.toString());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            handleException(e, request, response, out);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject requestData = new JsonObject();

        // Extract parameters from the request and log them
        addPropertyIfNotNull(requestData, "sort_title", request.getParameter("sort_title"));
        addPropertyIfNotNull(requestData, "sort_year", request.getParameter("sort_year"));
        addPropertyIfNotNull(requestData, "sort_director", request.getParameter("sort_director"));
        addPropertyIfNotNull(requestData, "sort_name", request.getParameter("sort_name"));

        log("Received POST request data: " + requestData.toString());

        response.getWriter().write(requestData.toString());
    }

    // Helper method to handle exceptions
    private void handleException(Exception e, HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("errorMessage", e.getMessage());

        out.write(errorResponse.toString());
        log("Error occurred:", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    // Helper method to add properties only if they are not null
    private void addPropertyIfNotNull(JsonObject json, String property, String value) {
        if (value != null) {
            json.addProperty(property, value);
        }
    }
}
