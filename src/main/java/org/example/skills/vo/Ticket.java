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
public class Ticket {

    private String orderNo;
    private String mealName;
    private int price;
    private int orderCount;
    private int index;

}
