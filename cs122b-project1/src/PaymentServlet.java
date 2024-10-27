import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // 设置返回类型为JSON
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // 获取用户输入的信用卡信息
        String cardNumber = request.getParameter("cardnumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String expDate = request.getParameter("expDate");
        String movieTitles = request.getParameter("movieTitles");

        System.out.println(movieTitles);
        try (Connection conn = dataSource.getConnection()) {
            // 查询信用卡信息是否存在并匹配
            String query = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardNumber);
            ResultSet rs = statement.executeQuery();

            String correctFirstName = "";
            String correctLastName = "";
            String correctExpDate = "";

            if (rs.next()) {
                correctFirstName = rs.getString("firstName");
                correctLastName = rs.getString("lastName");
                correctExpDate = rs.getString("expiration");
            }

            System.out.println("firstName" + firstName);
            System.out.println("correctFirstName" + correctFirstName);
            System.out.println("lastName" + lastName);
            System.out.println("correctLastName" + correctLastName);
            System.out.println(expDate);
            System.out.println(correctExpDate);

            if (firstName.equals(correctFirstName) && lastName.equals(correctLastName) && expDate.equals(correctExpDate)) {
                // 验证成功，处理订单
                processOrder(request, conn, responseJsonObject);
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Payment successful!");
                response.setStatus(200);
            } else {
                // 验证失败，返回错误信息
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid credit card details.");
                response.setStatus(400);
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", e.getMessage());
            response.setStatus(500);
            e.printStackTrace();
        }

        out.write(responseJsonObject.toString());
        out.close();
    }

    private void processOrder(HttpServletRequest request, Connection conn, JsonObject responseJsonObject) throws Exception {
        HttpSession session = request.getSession();
        Map<String, Integer> moviesInCart = (Map<String, Integer>) session.getAttribute("moviesInCart");

        if (moviesInCart == null || moviesInCart.isEmpty()) {
            throw new Exception("Cart is empty.");
        }

        String customerId = ((User) session.getAttribute("user")).getUserId();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        for (Map.Entry<String, Integer> entry : moviesInCart.entrySet()) {
            String movieId = entry.getKey();
            int quantity = entry.getValue();

            for (int i = 0; i < quantity; i++) {
                // 插入销售记录
                String insertQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, customerId);
                insertStatement.setString(2, movieId);
                insertStatement.setString(3, currentDate);
                insertStatement.executeUpdate();
                insertStatement.close();
            }
        }
    }
}
