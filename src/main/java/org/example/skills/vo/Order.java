package org.example.skills.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int orderNo;
    private int cuisineNo;
    private int mealNo;
    private int memberNo;
    private int orderCount;
    private int amount;
    private LocalDateTime orderDate;

    public Order(int mealNo, int memberNo, int orderCount, int amount){
        this.mealNo = mealNo;
        this.memberNo = memberNo;
        this.orderCount = orderCount;
        this.amount = amount;
    }
}
