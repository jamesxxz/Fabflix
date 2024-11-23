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
import java.util.HashMap;
import java.util.ArrayList;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/masterdb");
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
        System.out.println(cardNumber);
        System.out.println(movieTitles);

        String[] allmovies = movieTitles.split(";;");
        HttpSession session = request.getSession();
        try (Connection conn = dataSource.getConnection()) {
            // 查询信用卡信息是否存在并匹配
            String query = "SELECT * FROM creditcards WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cardNumber);
            ResultSet rs = statement.executeQuery();


            ArrayList<String> movieInfos = new ArrayList<>();

            for (int i = 0; i < allmovies.length; i++) {
                String[] movieInfo = allmovies[i].split("::");
                String query2 = "SELECT id FROM movies WHERE title = ?";
                PreparedStatement statement2 = conn.prepareStatement(query2);
                statement2.setString(1, movieInfo[0]);
                ResultSet rs1 = statement2.executeQuery();
                if (rs1.next()) {
                    movieInfos.add(rs1.getString("id") + "::" + movieInfo[1]);
                }
                rs1.close();
                statement2.close();
            }

            String correctFirstName = "";
            String correctLastName = "";
            String correctExpDate = "";

            if (rs.next()) {
                correctFirstName = rs.getString("firstName");
                correctLastName = rs.getString("lastName");
                correctExpDate = rs.getString("expiration");
            }

            if (firstName.equals(correctFirstName) && lastName.equals(correctLastName) && expDate.equals(correctExpDate)) {
                // 验证成功，处理订单
                //processOrder(request, conn, responseJsonObject, movieInfos);
                Map<String, Integer> salesIdMap = processOrder(request, conn, movieInfos);
                session.setAttribute("salesId", salesIdMap); // 将salesId存入session

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Payment successful!");
                response.setStatus(200);
            } else {
                // 验证失败，返回错误信息
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Invalid payment information.");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        out.write(responseJsonObject.toString());
        out.close();
    }

    private Map<String, Integer> processOrder(HttpServletRequest request, Connection conn, ArrayList<String> movieInfos) throws Exception {
        HttpSession session = request.getSession();
        Map<String, Integer> salesIdMap = new HashMap<>(); //newly added

        if (movieInfos.isEmpty()) {
            throw new Exception("Cart is empty.");
        }

        String customerId = ((User) session.getAttribute("user")).getUserId();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = formatter.format(new Date(System.currentTimeMillis()));

        for (String movieInfo : movieInfos) {
            String[] movieData = movieInfo.split("::");
            System.out.println(movieInfo);

            for (int i = 0; i < Integer.parseInt(movieData[1]); i++) {
                String insertQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery, new String[]{"salesid"});
                //PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, customerId);
                insertStatement.setString(2, movieData[0]);
                insertStatement.setString(3, currentDate);
                insertStatement.executeUpdate();
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int salesId = generatedKeys.getInt(1);
                    salesIdMap.put(movieData[0], salesId);
                }
                insertStatement.close();
            }
        }
        System.out.println("Sales ID Map: " + salesIdMap);

        return salesIdMap;
    }

}