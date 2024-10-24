import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@WebServlet("/hero-suggestion")
public class HeroSuggestion extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            log("DataSource lookup failed:", e);
        }
    }

    /**
     * Handles HTTP GET requests and returns a JSON response with movie suggestions.
     *
     * Example response:
     * [
     *   { "value": "Superman", "data": { "heroID": 101 } },
     *   { "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Prepare a JSON array to hold the suggestions
        JsonArray suggestions = new JsonArray();

        try {
            // Retrieve and sanitize the query parameter
            String query = request.getParameter("query");
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(suggestions.toString());
                return;
            }

            // Execute SQL query to search for matching movies
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = createPreparedStatement(conn, query);
                 ResultSet rs = stmt.executeQuery()) {

                // Build the JSON response from the result set
                while (rs.next()) {
                    String movieID = rs.getString("id");
                    String title = rs.getString("title");
                    suggestions.add(createSuggestionJson(movieID, title));
                }
            }

            // Write the JSON response back to the client
            response.getWriter().write(suggestions.toString());

        } catch (Exception e) {
            log("Error in doGet:", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Creates a prepared SQL statement with the query parameter.
     */
    private PreparedStatement createPreparedStatement(Connection conn, String query) throws Exception {
        String sql =
                "SELECT * FROM movies WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) " +
                        "OR title LIKE ? COLLATE utf8mb4_general_ci " +
                        "OR edth(title, ?, 2) = 1 LIMIT 10";

        PreparedStatement stmt = conn.prepareStatement(sql);
        String booleanQuery = "+" + String.join("*+", query.split(" ")) + "*";
        stmt.setString(1, booleanQuery);
        stmt.setString(2, "%" + query + "%");
        stmt.setString(3, query);

        return stmt;
    }

    /**
     * Creates a JSON object for a movie suggestion.
     * Example format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     */
    private static JsonObject createSuggestionJson(String heroID, String heroName) {
        JsonObject suggestion = new JsonObject();
        suggestion.addProperty("value", heroName);

        JsonObject data = new JsonObject();
        data.addProperty("heroID", heroID);

        suggestion.add("data", data);
        return suggestion;
    }
}
