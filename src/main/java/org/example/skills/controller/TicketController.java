package org.example.skills.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.skills.service.TicketService;
import org.example.skills.vo.Meal;
import org.example.skills.vo.Member;
import org.example.skills.vo.Order;
import org.example.skills.vo.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("cuisine")
    public String cuisine() {
        return "cuisine";
    }

    @GetMapping("passwd")
    public String manager() {
        return "passwd";
    }

    @GetMapping("manage")
    public String manage(Model model) {

        model.addAttribute("meals", ticketService.getMeals(0));
        return "manage";
    }

    @GetMapping("form")
    public String form(Model model, @RequestParam int cuisine) {

        List<Meal> meals = ticketService.getMeals(cuisine);
        model.addAttribute("cuisine", cuisine);
        model.addAttribute("meals", meals);

        List<Member> members = ticketService.getMembers();
        model.addAttribute("members", members);

        return "form";
    }

    @PostMapping("verify")
    @ResponseBody
    public boolean verify(int memberNo, String passwd){
//        System.out.println(memberNo + "  " + passwd + " " + ticketService.verifyMember(memberNo, passwd));
        return ticketService.verifyMember(memberNo, passwd);
    }

    @PostMapping("ticket")
    public String ticket(Model model, @RequestParam("order") String order, @RequestParam int cuisineNo,
                         @RequestParam int memberNo) throws JsonProcessingException {

        System.out.println("cuisineNo: " + cuisineNo + " memberNo: " + memberNo);

        LocalDateTime now = LocalDateTime.now();
        ObjectMapper mapper = new ObjectMapper();
        List<Order> orders =
                mapper.readValue(order, new TypeReference<List<Order>>() {});

        for(Order o : orders){
            int orderRs = ticketService.order(cuisineNo, o.getMealNo(), memberNo, o.getOrderCount(), o.getAmount(), now);
        }

        List<Ticket> tickets = new ArrayList<>();
        for(Order o : orders){
            for (int i = 0; i < o.getOrderCount(); i++) {
                tickets.add(new Ticket(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + memberNo + "-" + 1,
                        ticketService.getMealName(o.getMealNo()),
                        ticketService.getPrice(o.getMealNo()), o.getOrderCount(), i+1));
            }
        }
        model.addAttribute("tickets", tickets);

        return "ticket";
    }

    @PostMapping("registerMenu")
    @ResponseBody
    public boolean registerMenu(int mealNo, int cuisine, String mealName, int price, int maxCount){

        if(mealNo == 0) return ticketService.registerMenu(cuisine, mealName, price, maxCount);
        return ticketService.updateMenu(mealNo, cuisine, mealName, price, maxCount);
    }

    @GetMapping("getMeals")
    @ResponseBody
    public List<Meal> getMeals(int cuisine){
        return ticketService.getMeals(cuisine);
    }

    @PostMapping("editMeal")
    @ResponseBody
    public boolean editMeal(Meal meal){
//        System.out.println(meal.getMealNo());
        return ticketService.editMeal(meal);
    }

    @DeleteMapping("deleteMeal")
    @ResponseBody
    public boolean deleteMeal(int[] mealNos){
        for(int i : mealNos){
            if(!ticketService.deleteMenu(i)) return false;
        }
        return true;
    }

    @PostMapping("todayMeals")
    @ResponseBody
    public boolean todayMeals(int[] mealNos){
        if(mealNos.length > 25) return false;
        ticketService.updateTodayMeals(mealNos);
        return true;

    }

    @GetMapping("getOrders")
    @ResponseBody
    public List<Order> getOrders(String keyword){
        return ticketService.getOrders(keyword);
    }

    @GetMapping("getChart")
    @ResponseBody
    public List<Map<String, Object>> getChart(){
        return ticketService.getOrderCountsByCuisine();
    }

    @GetMapping("member")
    public String getMember(Model model) {
        model.addAttribute("lastMemberNo", ticketService.getLastMember() + 1);
        return "member";
    }

    @PostMapping("member")
    @ResponseBody
    public boolean registerMember(Member member){
        return ticketService.registerMember(member.getMemberName(), member.getPasswd());
    }
}
