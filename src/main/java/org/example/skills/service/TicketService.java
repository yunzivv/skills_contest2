// src/main/java/org/example/skills/service/MemberService.java
package org.example.skills.service;

import jakarta.transaction.Transactional;
import org.example.skills.vo.Cuisine;
import org.example.skills.vo.Meal;
import org.example.skills.vo.Member;
import org.example.skills.vo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Member> memberRowMapper = (rs, i) -> new Member(
            rs.getInt("memberNo"),
            rs.getString("memberName"),
            rs.getString("passwd")
    );

    private final RowMapper<Cuisine> cuisineRowMapper = (rs, i) -> new Cuisine(
            rs.getInt("cuisineNo"),
            rs.getString("cuisineName")
    );

    private final RowMapper<Meal> mealRowMapper = (rs, i) -> new Meal(
            rs.getInt("mealNo"),
            rs.getInt("cuisineNo"),
            rs.getString("mealName"),
            rs.getInt("price"),
            rs.getInt("maxCount"),
            rs.getInt("todayMeal")
    );

    private final RowMapper<Order> orderRowMapper = (rs, i) -> new Order(
            rs.getInt("orderNo"),
            rs.getInt("cuisineNo"),
            rs.getString("cuisineName"),
            rs.getInt("mealNo"),
            rs.getString("mealName"),
            rs.getInt("memberNo"),
            rs.getString("memberName"),
            rs.getInt("orderCount"),
            rs.getInt("amount"),
            rs.getTimestamp("orderDate").toLocalDateTime()
    );

    public List<Meal> getMeals(int cuisine) {
        String sql = "SELECT * FROM meal WHERE (? = 0 OR cuisineNo = ?)";
        return jdbc.query(sql, mealRowMapper, cuisine, cuisine);
    }

    public List<Member> getMembers() {
        String sql = "SELECT * FROM `member`";
        return jdbc.query(sql, memberRowMapper);
    }

    public boolean verifyMember(int memberNo, String passwd) {
        String sql = "SELECT COUNT(*) FROM member WHERE memberNo = ? AND passwd = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, memberNo, passwd);
        return count != null && count == 1;
    }

    public int order(int cuisineNo, int mealNo, int memberNo, int orderCount, int amount, LocalDateTime now) {
        String sql = "INSERT INTO `order` (cuisineNo, mealNo, memberNo, orderCount, amount, orderDate) VALUES (?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbc.update(sql, cuisineNo, mealNo, memberNo, orderCount, amount, now);
        return rowsAffected;
    }

    public String getMealName(int mealNo) {
        String sql = "SELECT mealName FROM meal WHERE mealNo = ?";
        return jdbc.queryForObject(sql, String.class, mealNo);
    }

    public int getPrice(int mealNo) {
        String sql = "SELECT price FROM meal WHERE mealNo = ?";
        return jdbc.queryForObject(sql, Integer.class, mealNo);
    }

    public boolean registerMenu(int cuisineNo, String mealName, int price, int maxCount) {
        String sql = "INSERT INTO meal (cuisineNo, mealName, price, maxCount, todayMeal) VALUES (?, ?, ?, ?, ?)";
        return jdbc.update(sql, String.valueOf(cuisineNo), mealName, price, maxCount, 1) == 1;
    }

    public boolean deleteMenu(int mealNo) {
        String sql = "DELETE FROM meal WHERE mealNo = ?";
        return jdbc.update(sql, mealNo) == 1;
    }

    @Transactional
    public boolean updateTodayMeals(int[] mealNos) {

        jdbc.update("UPDATE meal SET todayMeal = 0");

        if (mealNos.length == 0) return true;

        String placeholders = String.join(",",
                Collections.nCopies(mealNos.length, "?"));

        String sql = "UPDATE meal SET todayMeal = 1 WHERE mealNo IN (" + placeholders + ")";
        Object[] params = Arrays.stream(mealNos).boxed().toArray();

        jdbc.update(sql, params);

        return true;
    }

    public boolean editMeal(Meal meal) {
        String sql = "UPDATE meal SET cuisineNo = ?, mealName = ?, price = ?, maxCount = ? WHERE mealNo = ?";
        return jdbc.update(sql, meal.getCuisineNo(), meal.getMealName(), meal.getPrice(), meal.getMaxCount(), meal.getMealNo()) == 1;
    }

    public boolean updateMenu(int mealNo, int cuisine, String mealName, int price, int maxCount) {
        String sql = "UPDATE meal SET cuisineNo = ?, mealName = ?, price = ?, maxCount = ? WHERE mealNo = ?";
        return jdbc.update(sql, cuisine, mealName, price, maxCount, mealNo) == 1;
    }

    public List<Order> getOrders(String keyword) {

        String sql = "SELECT cuisineName, mealName, memberName, `order`.* " +
                "FROM `order` " +
                "JOIN cuisine ON `order`.cuisineNo = cuisine.cuisineNo " +
                "JOIN meal ON `order`.mealNo = meal.mealNo " +
                "JOIN `member` ON `order`.memberNo = `member`.memberNo " +
                "WHERE (? = '' OR mealName LIKE ?)";

        String likeKeyword = "%" + keyword + "%";

        return jdbc.query(sql, orderRowMapper, keyword, likeKeyword);
    }

    public List<Map<String, Object>> getOrderCountsByCuisine() {
        String sql = "SELECT cuisine.cuisineName, COUNT(*) AS cnt FROM `order` " +
                "JOIN cuisine ON `order`.cuisineNo = cuisine.cuisineNo GROUP BY `order`.cuisineNo";
        return jdbc.queryForList(sql);
    }

    public int getLastMember() {
        String sql = "SELECT memberNo FROM `member` ORDER BY memberNo DESC LIMIT 1";
        return jdbc.queryForObject(sql, Integer.class);
    }

    public boolean registerMember(String memberName, String passwd) {
        String sql = "INSERT INTO `member` (memberName, passwd) VALUES (?, ?)";
        return jdbc.update(sql, memberName, passwd) == 1;
    }
}
