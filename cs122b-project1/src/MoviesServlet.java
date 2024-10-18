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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "WITH TopGenres AS ( " +
                            "SELECT g.name, gim.movieId, " +
                            "ROW_NUMBER() OVER (PARTITION BY gim.movieId ORDER BY g.name) AS genreRank " +
                            "FROM genres_in_movies gim " +
                            "JOIN genres g ON gim.genreId = g.id)," +
                            "TopStars AS ( " +
                            "SELECT s.name, sim.movieId, s.id, " +
                            "ROW_NUMBER() OVER (PARTITION BY sim.movieId ORDER BY s.name) AS starRank " +
                            "FROM stars_in_movies sim " +
                            "JOIN stars s ON sim.starId = s.id) " +

                            "SELECT m.id, m.title, m.year, m.director, " +
                            "(SELECT GROUP_CONCAT(g.name SEPARATOR ', ') " +
                            "FROM TopGenres g WHERE g.movieId = m.id AND g.genreRank <= 3) AS genres, " +
                            "(SELECT GROUP_CONCAT(s.name SEPARATOR ', ') " +
                            "FROM TopStars s WHERE s.movieId = m.id AND s.starRank <= 3) AS stars, " +
                            "(SELECT GROUP_CONCAT(s.id SEPARATOR ', ') " +
                            "FROM TopStars s WHERE s.movieId = m.id AND s.starRank <= 3) AS starIds, " +
                            "r.rating " +
                            "FROM movies m " +
                            "JOIN ratings r ON m.id = r.movieId " +
                            "ORDER BY r.rating DESC " +
                            "LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_yr = rs.getString("year");
                String movie_director = rs.getString("director");
                String genres = rs.getString("genres");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");
                String starIds = rs.getString("starIds");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_yr", movie_yr);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("starIds", starIds);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
