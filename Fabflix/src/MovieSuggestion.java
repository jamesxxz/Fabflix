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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // setup the response json array
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            System.out.println(query);
            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            Connection conn = dataSource.getConnection();
            String suggestionQuery = "SELECT * FROM movies WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE)"
                    + " LIMIT 10";
            PreparedStatement prepStmt = conn.prepareStatement(suggestionQuery);
            List<String> valLst = List.of(query.split(" "));
            List<String> resLst = valLst.stream()
                    .map(str -> "+" + str + "*")
                    .collect(Collectors.toList());
            String fulltextStr = String.join(" ", resLst);
            prepStmt.setString(1, fulltextStr);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String movieYear = rs.getString("year");
                jsonArray.add(generateJsonObject(id, title, movieYear));
            }
            rs.close();
            prepStmt.close();
            conn.close();
            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    private static JsonObject generateJsonObject(String movieID, String movieTitle, String movieYear) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle + " (" + movieYear + ")");

        JsonObject dataJsonObject = new JsonObject();
        dataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", dataJsonObject);
        return jsonObject;
    }

}