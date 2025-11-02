package org.example.skills.controller;

import org.example.skills.service.APIService;
import org.example.skills.vo.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    private APIService apiService;

    @ResponseBody
    @GetMapping("/ping")
    public String ping() { return "pong"; }

    @PostMapping("/login")
    public boolean login(@RequestParam String name, @RequestParam String passwd) {

        return apiService.login(name, passwd);
    }

    @PostMapping("/register")
    public boolean register(@RequestParam String name, @RequestParam String birth, @RequestParam String tel,
                            @RequestParam(required = false) String address, @RequestParam(required = false) String company) {

        if(name == null || birth == null || tel == null)return false;
        if(name.trim().isEmpty() || birth.trim().isEmpty() || tel.trim().isEmpty()) return false;

        String code = "S25";
        String[] birthStr = birth.split("-");
        code += (Integer.parseInt(birthStr[0]) + Integer.parseInt(birthStr[1]) + Integer.parseInt(birthStr[2]));

        return apiService.register(code, name, birth, tel, address, company);
    }

    @GetMapping("/customer")
    public List<Customer> customers(@RequestParam(required = false) String keyword) {
        return apiService.getCustomers(keyword);
    }

    @PostMapping("/customer")
    public boolean update(@RequestParam String code, String name, String birth, String tel, String address, String company) {
        return apiService.updateCustomer(code, name, birth, tel, address, company);
    }

}
