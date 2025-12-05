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


//    private final RowMapper<Customer> customerRowMapper = (rs, i) -> new Customer(
//            rs.getString("code"),
//            rs.getString("name"),
//            rs.getDate("birth"),
//            rs.getString("tel"),
//            rs.getString("address"),
//            rs.getString("company")
//    );
//
//    private final RowMapper<Contract> contractRowMapper = (rs, i) -> new Contract(
//            rs.getString("contractName")
//    );
//
//    private final RowMapper<Contract> contractRowMapperAll = (rs, i) -> new Contract(
//            rs.getString("customerCode"),
//            rs.getString("contractName"),
//            rs.getInt("regPrice"),
//            rs.getDate("regDate"),
//            rs.getInt("monthPrice"),
//            rs.getString("adminName")
//    );
//
//    private final RowMapper<Admin> adminRowMapper = (rs, i) -> new Admin(
//            rs.getString("name")
//    );
//
//
//    public boolean login(String name, String passwd) {
//        Boolean ok = jdbc.queryForObject(
//                "SELECT EXISTS(SELECT 1 FROM admin WHERE name=? AND passwd=? LIMIT 1)",
//                Boolean.class,
//                name, passwd
//        );
//        return Boolean.TRUE.equals(ok);
//    }
//
//    public boolean register(String code, String name, String birth, String tel, String address, String company) {
//
//        String sql = "INSERT INTO customer (code, name, birth, tel, address, company) VALUES (?, ?, ?, ?, ?, ?)";
//        int rowsAffected = jdbc.update(sql, code, name, java.sql.Date.valueOf(birth), tel, address, company);
//
//        return rowsAffected == 1;
//    }


//    public Customer getCustomer(String name) {
//        if(name == null || name.trim().isEmpty()) {
//            return jdbc.queryForObject("SELECT * FROM customer LIMIT 1", customerRowMapper);
//        }
//        return jdbc.queryForObject("SELECT * FROM customer WHERE name = ? LIMIT 1", customerRowMapper, name);
//    }
//
//    public boolean updateCustomer(String code, String name, String birth, String tel, String address, String company) {
//
//        String sql = "UPDATE customer SET birth = ?, tel = ?, address = ?, company = ?  WHERE code = ? AND name = ?";
//        int rowsAffected = jdbc.update(sql, java.sql.Date.valueOf(birth), tel, address, company, code, name);
//
//        return rowsAffected == 1;
//    }
//
//    public boolean deleteCustomer(String code, String name) {
//
//        String sql = "DELETE FROM customer WHERE code = ? AND name = ?";
//        int rowsAffected = jdbc.update(sql, code, name);
//
//        return rowsAffected == 1;
//    }
//
//    public List<Contract> getContract() {
//        return jdbc.query("SELECT DISTINCT contractName FROM contract", contractRowMapper);
//    }
//
//    public List<Contract> getContracts(String customercode) {
//        return jdbc.query("SELECT * FROM contract WHERE customercode = ?", contractRowMapperAll, customercode);
//    }
//
//    public List<Admin> getAdmin() {
//        return jdbc.query("SELECT name FROM admin", adminRowMapper);
//    }
//
//    public boolean insertContract(String customerCode, String contractName, int regPrice, int monthPrice, String adminName) {
//        Date date = java.sql.Date.valueOf(LocalDate.now());
//        String sql = "INSERT INTO contract (customerCode, contractName, regPrice, regDate, monthPrice, adminName) VALUES (?, ?, ?, ?, ?, ?)";
//        int rowsAffected = jdbc.update(sql, customerCode, contractName, regPrice, date, monthPrice, adminName);
//
//        return rowsAffected == 1;
//    }
//
//    public boolean deleteContract(String customerCode, String contractName, String regDate) {
//        String sql = "DELETE FROM contract WHERE customerCode = ? AND contractName = ? AND regDate = ? LIMIT 1";
//        int rowsAffected = jdbc.update(sql, customerCode, contractName, java.sql.Date.valueOf(regDate));
//
//        return rowsAffected == 1;
//    }
}
