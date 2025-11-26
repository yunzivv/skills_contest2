package org.example.skills.controller;

import org.example.skills.service.APIService;
import org.example.skills.vo.Meal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class PageController {

    private APIService apiService;

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("cuisine")
    public String cuisine() {
        return "cuisine";
    }

    @GetMapping("ticket")
    public String ticket(@RequestParam int cuisine) {

        List<Meal> meals = apiService.getMeals(cuisine);

        return "ticket";
    }
}
