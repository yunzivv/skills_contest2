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
    private String cuisineName;
    private int mealNo;
    private String mealName;
    private int memberNo;
    private String memberName;
    private int orderCount;
    private int amount;
    private LocalDateTime orderDate;

}