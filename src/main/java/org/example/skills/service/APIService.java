// src/main/java/org/example/skills/service/MemberService.java
package org.example.skills.service;

import org.example.skills.vo.Admin;
import org.example.skills.vo.Contract;
import org.example.skills.vo.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIService {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Customer> customerRowMapper = (rs, i) -> new Customer(
            rs.getString("code"),
            rs.getString("name"),
            rs.getDate("birth"),
            rs.getString("tel"),
            rs.getString("address"),
            rs.getString("company")
    );

    private final RowMapper<Contract> contractRowMapper = (rs, i) -> new Contract(
//            rs.getString("customerCode"),
            rs.getString("contractName")
//            rs.getInt("regPrice"),
//            rs.getDate("regDate"),
//            rs.getInt("monthPrice"),
//            rs.getString("adminName")
    );

    private final RowMapper<Admin> adminRowMapper = (rs, i) -> new Admin(
            rs.getString("name")
    );

    public boolean login(String name, String passwd) {
        Boolean ok = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM admin WHERE name=? AND passwd=? LIMIT 1)",
                Boolean.class,
                name, passwd
        );
        return Boolean.TRUE.equals(ok);
    }

    public boolean register(String code, String name, String birth, String tel, String address, String company) {

        String sql = "INSERT INTO customer (code, name, birth, tel, address, company) VALUES (?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbc.update(sql, code, name, java.sql.Date.valueOf(birth), tel, address, company);

        return rowsAffected == 1;
    }

    public List<Customer> getCustomers(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return jdbc.query("SELECT * FROM customer", customerRowMapper);
        }

        String sql = "SELECT * FROM customer WHERE `name` LIKE CONCAT('%', ?, '%')";
        List<Customer> customers = jdbc.query(sql, customerRowMapper, keyword);
        return customers;
    }

    public Customer getCustomer(String name) {
        if(name == null || name.trim().isEmpty()) {
            return jdbc.queryForObject("SELECT * FROM customer LIMIT 1", customerRowMapper);
        }
        return jdbc.queryForObject("SELECT * FROM customer WHERE name = ? LIMIT 1", customerRowMapper, name);
    }

    public boolean updateCustomer(String code, String name, String birth, String tel, String address, String company) {

        String sql = "UPDATE customer SET birth = ?, tel = ?, address = ?, company = ?  WHERE code = ? AND name = ?";
        int rowsAffected = jdbc.update(sql, java.sql.Date.valueOf(birth), tel, address, company, code, name);

        return rowsAffected == 1;
    }

    public boolean deleteCustomer(String code, String name) {

        String sql = "DELETE FROM customer WHERE code = ? AND name = ?";
        int rowsAffected = jdbc.update(sql, code, name);

        return rowsAffected == 1;
    }

    public List<Contract> getContract() {
        return jdbc.query("SELECT DISTINCT contractName FROM contract", contractRowMapper);
    }

    public List<Admin> getAdmin() {
        return jdbc.query("SELECT name FROM admin", adminRowMapper);
    }
}
