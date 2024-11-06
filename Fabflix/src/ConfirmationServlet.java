import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // 设置响应类型为 JSON
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        Map<String, Integer> salesIdMap = (Map<String, Integer>) session.getAttribute("salesId");

        JsonArray jsonArray = new JsonArray();

        try (Connection conn = dataSource.getConnection()) {
            for (String movieId : salesIdMap.keySet()) {
                String query = "SELECT title FROM movies WHERE id = ?";
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.setString(1, movieId);
                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        JsonObject movieInfo = new JsonObject();
                        movieInfo.addProperty("movieTitle", rs.getString("title"));
                        movieInfo.addProperty("salesId", salesIdMap.get(movieId));
                        jsonArray.add(movieInfo);
                    }
                    rs.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            return;
        }

        out.write(jsonArray.toString());
        response.setStatus(200);
    }
}
