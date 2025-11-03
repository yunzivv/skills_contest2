package org.example.skills.controller;

import org.example.skills.service.APIService;
import org.example.skills.vo.Admin;
import org.example.skills.vo.Contract;
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

        if(isEmpty(name) || isEmpty(birth) || isEmpty(tel) || isEmpty(address) || isEmpty(company)) return false;

        String code = "S25";
        String[] birthStr = birth.split("-");
        code += (Integer.parseInt(birthStr[0]) + Integer.parseInt(birthStr[1]) + Integer.parseInt(birthStr[2]));

        return apiService.register(code, name, birth, tel, address, company);
    }

    @GetMapping("/customer")
    public List<Customer> list(@RequestParam(required = false) String keyword) {
        return apiService.getCustomers(keyword);
    }

    @GetMapping("/customer/{name}")
    public Customer getCustomer(@PathVariable(required = false) String name) {
        return apiService.getCustomer(name);
    }

    @PostMapping("/customer")
    public boolean update(@RequestParam String code, String name, String birth, String tel, String address, String company) {
        if(isEmpty(code) || isEmpty(name) || isEmpty(birth) || isEmpty(tel) || isEmpty(address) || isEmpty(company)) return false;
        return apiService.updateCustomer(code, name, birth, tel, address, company);
    }

    @DeleteMapping("/customer")
    public boolean delete(@RequestParam String code, String name) {
        if(isEmpty(code) || isEmpty(name)) return false;
        return apiService.deleteCustomer(code, name);
    }


    @GetMapping("/contract")
    public List<Contract> getContract() {
        return apiService.getContract();
    }

    @GetMapping("/contract/{code}")
    public List<Contract> getContracts(@PathVariable String code) {
        return apiService.getContracts(code);
    }


    @GetMapping("/admin")
    public List<Admin> getAdmin() {
        return apiService.getAdmin();
    }


    boolean isEmpty(String str){
        return str == null || str.trim().isEmpty();
    }

}
