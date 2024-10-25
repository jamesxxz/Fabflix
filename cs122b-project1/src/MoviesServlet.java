//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import jakarta.servlet.ServletConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//
//// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
//@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
//public class MoviesServlet extends HttpServlet {
//    private static final long serialVersionUID = 3L;
//
//    // Create a dataSource which registered in web.
//    private DataSource dataSource;
//
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//     */
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        response.setContentType("application/json"); // Response mime type
//
//        // Output stream to STDOUT
//        PrintWriter out = response.getWriter();
//
//        // Get a connection from dataSource and let resource manager close the connection after usage.
//        try (Connection conn = dataSource.getConnection()) {
//
//            // Declare our statement
//            Statement statement = conn.createStatement();
//
//            String query = "WITH TopGenres AS ( " +
//                            "SELECT g.name, gim.movieId, " +
//                            "ROW_NUMBER() OVER (PARTITION BY gim.movieId ORDER BY g.name) AS genreRank " +
//                            "FROM genres_in_movies gim " +
//                            "JOIN genres g ON gim.genreId = g.id)," +
//                            "TopStars AS ( " +
//                            "SELECT s.name, sim.movieId, s.id, " +
//                            "ROW_NUMBER() OVER (PARTITION BY sim.movieId ORDER BY s.name) AS starRank " +
//                            "FROM stars_in_movies sim " +
//                            "JOIN stars s ON sim.starId = s.id) " +
//
//                            "SELECT m.id, m.title, m.year, m.director, " +
//                            "(SELECT GROUP_CONCAT(g.name SEPARATOR ', ') " +
//                            "FROM TopGenres g WHERE g.movieId = m.id AND g.genreRank <= 3) AS genres, " +
//                            "(SELECT GROUP_CONCAT(s.name SEPARATOR ', ') " +
//                            "FROM TopStars s WHERE s.movieId = m.id AND s.starRank <= 3) AS stars, " +
//                            "(SELECT GROUP_CONCAT(s.id SEPARATOR ', ') " +
//                            "FROM TopStars s WHERE s.movieId = m.id AND s.starRank <= 3) AS starIds, " +
//                            "r.rating " +
//                            "FROM movies m " +
//                            "JOIN ratings r ON m.id = r.movieId " +
//                            "ORDER BY r.rating DESC " +
//                            "LIMIT 20;";
//
//            // Perform the query
//            ResultSet rs = statement.executeQuery(query);
//
//            JsonArray jsonArray = new JsonArray();
//
//            // Iterate through each row of rs
//            while (rs.next()) {
//                String movieId = rs.getString("id");
//                String movie_title = rs.getString("title");
//                String movie_yr = rs.getString("year");
//                String movie_director = rs.getString("director");
//                String genres = rs.getString("genres");
//                String stars = rs.getString("stars");
//                String rating = rs.getString("rating");
//                String starIds = rs.getString("starIds");
//
//                // Create a JsonObject based on the data we retrieve from rs
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("movie_id", movieId);
//                jsonObject.addProperty("movie_title", movie_title);
//                jsonObject.addProperty("movie_yr", movie_yr);
//                jsonObject.addProperty("movie_director", movie_director);
//                jsonObject.addProperty("genres", genres);
//                jsonObject.addProperty("stars", stars);
//                jsonObject.addProperty("rating", rating);
//                jsonObject.addProperty("starIds", starIds);
//
//                jsonArray.add(jsonObject);
//            }
//            rs.close();
//            statement.close();
//
//            // Log to localhost log
//            request.getServletContext().log("getting " + jsonArray.size() + " results");
//
//            // Write JSON string to output
//            out.write(jsonArray.toString());
//            // Set response status to 200 (OK)
//            response.setStatus(200);
//
//        } catch (Exception e) {
//
//            // Write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
//        } finally {
//            out.close();
//        }
//
//        // Always remember to close db connection after usage. Here it's done by try-with-resources
//
//    }
//}

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
import java.util.Objects;

// 将这个 Servlet 映射到 /api/movies URL
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            // 初始化数据源，连接到数据库
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // 处理 GET 请求
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // 设置响应类型为 JSON
        PrintWriter out = response.getWriter(); // 获取输出流，用于将响应写入前端

        // 获取查询参数
        String pageNumber = request.getParameter("page");
        String numberPerPage = request.getParameter("num");
        String sort = request.getParameter("sort");
        String input = request.getParameter("input");

        // 设置默认值，避免空指针异常
        pageNumber = (pageNumber == null) ? "1" : pageNumber;
        numberPerPage = (numberPerPage == null) ? "10" : numberPerPage;
        sort = (sort == null) ? "r0t1" : sort;

        String sortOrder = buildSortOrder(sort); // 构建排序语句
        String inputQuery = buildInputQuery(input); // 构建搜索条件语句

        try (Connection conn = dataSource.getConnection()) {
            // 构建 SQL 查询
            String query = "SELECT m.id, m.title, m.year, m.director, " +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC) AS genres, " +
                    "GROUP_CONCAT(DISTINCT s.name ORDER BY s.name ASC) AS stars, " +
                    "r.rating " +
                    "FROM movies m " +
                    "LEFT JOIN ratings r ON m.id = r.movieId " +
                    "JOIN genres_in_movies gim ON m.id = gim.movieId " +
                    "JOIN genres g ON gim.genreId = g.id " +
                    "JOIN stars_in_movies sim ON m.id = sim.movieId " +
                    "JOIN stars s ON sim.starId = s.id " +
                    "WHERE " + inputQuery +
                    " GROUP BY m.id, m.title, m.year, m.director, r.rating " +
                    "ORDER BY " + sortOrder +
                    " LIMIT ? OFFSET ?";

            // 准备查询语句
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(numberPerPage));
            statement.setInt(2, (Integer.parseInt(pageNumber) - 1) * Integer.parseInt(numberPerPage));

            // 执行查询
            ResultSet rs = statement.executeQuery();

            // 将查询结果转换为 JSON 数组
            JsonArray jsonArray = new JsonArray();
            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", rs.getString("id"));
                jsonObject.addProperty("movie_title", rs.getString("title"));
                jsonObject.addProperty("movie_yr", rs.getString("year"));
                jsonObject.addProperty("movie_director", rs.getString("director"));
                jsonObject.addProperty("genres", rs.getString("genres"));
                jsonObject.addProperty("stars", rs.getString("stars"));
                jsonObject.addProperty("rating", rs.getString("rating"));
                jsonArray.add(jsonObject);
            }

            // 关闭资源
            rs.close();
            statement.close();

            // 写入 JSON 响应
            out.write(jsonArray.toString());
            response.setStatus(200); // 设置响应状态为 OK
        } catch (Exception e) {
            // 处理异常，并返回错误信息
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500); // 设置响应状态为 Internal Server Error
        } finally {
            out.close(); // 关闭输出流
        }
    }

    // 构建排序语句
    private String buildSortOrder(String sort) {
        String sortOrder = "";
        if (sort.startsWith("t0")) sortOrder = "m.title DESC";
        else if (sort.startsWith("t1")) sortOrder = "m.title ASC";
        else if (sort.startsWith("r0")) sortOrder = "r.rating DESC";
        else if (sort.startsWith("r1")) sortOrder = "r.rating ASC";

        sortOrder += ", ";
        if (sort.endsWith("t0")) sortOrder += "m.title DESC";
        else if (sort.endsWith("t1")) sortOrder += "m.title ASC";
        else if (sort.endsWith("r0")) sortOrder += "r.rating DESC";
        else if (sort.endsWith("r1")) sortOrder += "r.rating ASC";

        return sortOrder;
    }

    // 构建输入条件查询语句
    private String buildInputQuery(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "1=1"; // 如果没有输入条件，返回恒为真的条件
        }

        StringBuilder inputQuery = new StringBuilder();
        String[] conditions = input.split(":");
        for (int i = 0; i < conditions.length; i += 2) {
            if (i > 0) inputQuery.append(" AND ");
            String field = conditions[i];
            String value = conditions[i + 1];
            switch (field) {
                case "title":
                    inputQuery.append("m.title LIKE '%").append(value).append("%'");
                    break;
                case "year":
                    inputQuery.append("m.year = ").append(value);
                    break;
                case "director":
                    inputQuery.append("m.director LIKE '%").append(value).append("%'");
                    break;
                case "name":
                    inputQuery.append("s.name LIKE '%").append(value).append("%'");
                    break;
                case "genre":
                    inputQuery.append("g.name = '").append(value).append("'");
                    break;
                case "alpha":
                    if (Objects.equals(value, "*")) {
                        inputQuery.append("LEFT(m.title, 1) REGEXP '[^a-zA-Z0-9]'");
                    } else {
                        inputQuery.append("LEFT(m.title, 1) = '").append(value).append("'");

                    }
            }
        }
        return inputQuery.toString();
    }
}
