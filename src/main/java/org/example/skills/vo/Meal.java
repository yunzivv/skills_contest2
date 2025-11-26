package org.example.skills.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    private int cuisineNo;
    private  String mealName;
    private int price;
    private  int maxCount;
    private  int todayMeal;

}
