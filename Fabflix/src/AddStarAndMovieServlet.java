import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;

@WebServlet(name = "AddStarAndMovieServlet", urlPatterns = "/_dashboard/api/add")
public class AddStarAndMovieServlet extends HttpServlet {
    private DataSource dataSource;

    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private String createNewId(String maxId) {
        int maxIDnum = Integer.parseInt(maxId.replace("nm", ""));
        String newStarId = "nm" + (maxIDnum + 1);
        return newStarId;
    }

    public void insertNewStar(Connection conn, String newStarId, String starName, String birthYear) throws SQLException {
        String insertQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?,?,?)";
        PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
        insertStatement.setString(1, newStarId);
        insertStatement.setString(2, starName);
        if (Objects.equals(birthYear, "")) {
            insertStatement.setString(3, null);
        } else {
            insertStatement.setString(3, birthYear);
        }
        insertStatement.executeUpdate();
        insertStatement.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject respJsonObj = new JsonObject();


        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");

        System.out.println(starName);
        System.out.println(birthYear);

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT max(id) as maxId FROM stars";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String newStarId = createNewId(rs.getString(1));
            rs.close();
            statement.close();

            insertNewStar(conn, newStarId, starName, birthYear);
            respJsonObj.addProperty("status", "success");
            respJsonObj.addProperty("message", "Successfully added! Star ID: " + newStarId);
        } catch (Exception e) {
            respJsonObj.addProperty("status", "error");
            respJsonObj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        out.write(respJsonObj.toString());
        out.close();
    }
}
