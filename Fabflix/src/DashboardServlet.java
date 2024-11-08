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

@WebServlet(name = "DashboardServlet", urlPatterns = "/_dashboard/api/dashboard")
public class DashboardServlet extends HttpServlet {
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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JsonArray tableArray = new JsonArray();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT TABLE_NAME AS `Table`, COLUMN_NAME AS `Column`, DATA_TYPE AS `Type` " +
                    "FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = \"moviedb\" " +
                    "ORDER BY TABLE_NAME, ORDINAL_POSITION;";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getString("Table"));
                System.out.println(rs.getString("Column"));
                System.out.println(rs.getString("Type"));
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("tableName", rs.getString("Table"));
                jsonObj.addProperty("columnName", rs.getString("Column"));
                jsonObj.addProperty("dataType", rs.getString("Type"));
                tableArray.add(jsonObj);
            }
            rs.close();
            statement.close();
            out.write(tableArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
